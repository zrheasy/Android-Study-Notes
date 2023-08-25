# 蓝牙协议解析

蓝牙分为：经典蓝牙和低功耗蓝牙(BLE)

**BLE蓝牙协议栈分为3层：**

- Application
- Host
- Controllers

其中Host和Controller属于蓝牙核心层协议，Application属于应用层协议

**Controller包含以下协议层：**

- HCI（Host Control Interface）主机控制接口
- LL（Link Logic）链路层
- PHY（Physical Layer）物理层

**Host由以下协议栈组成：**

- GAP（Generic Access Profile）通用访问配置文件层，主要用来进行广播、扫描和连接，保证各个蓝牙设间备能够互相发现和连接。
- SM（Security Manager）安全管理层
- GATT（Generic Attribute Profile）通用属性配置文件层，用于规范attribute中的数据内容，并运用group的概念对attribute进行管理。
- ATT（Attribute Protocol）属性协议层，BLE引入attribute用来描述一条条数据，ATT层即是对attribute的数据和命令进行定义。
- L2CAP（Logical Link Control And Adaptation Protocol）逻辑链路控制及自适应层

**BLE将蓝牙设备根据场景分配为不同的角色**

- 当使用手机等设备进行蓝牙扫描时，手机即为Central Device也称中央设备，被扫描的设备即为Peripheral Device也称外围设备
- 当使用手机连接蓝牙设备时，手机即为Client客户端设备，被连接的蓝牙设备即为Server服务端设备

## BLE广播包

BLE广播包分为两种：Advertising Data广播包和Scan Response响应包 当主机主动扫描时会发送扫描请求给从机，从机收到请求后会返回响应包数据。

BLE广播包数据长度为31字节，有效数据长度不足时会补充0填满，有效数据部分包含多个广播数据单元称为AD Structure，AD Structure由Len和Data组成，第一个字节为Len表示Data的长度，Data的第一个字节为AD
Type表示数据的类型，后面的Len-1个字节为AD Data。

**AD Type通常包含以下类型：**

设备标记：

- FLAGS = 0x01；

服务UUID:

- SERVICE_UUIDS_16_BIT_PARTIAL = 0x02；
- SERVICE_UUIDS_16_BIT_COMPLETE = 0x03;
- SERVICE_UUIDS_32_BIT_PARTIAL = 0x04;
- SERVICE_UUIDS_32_BIT_COMPLETE = 0x05;
- SERVICE_UUIDS_128_BIT_PARTIAL = 0x06;
- SERVICE_UUIDS_128_BIT_COMPLETE = 0x07;

设备名称：

- LOCAL_NAME_SHORT = 0x08;
- LOCAL_NAME_COMPLETE = 0x09;

信号强度：

- TX_POWER_LEVEL = 0x0A;

服务数据：

- SERVICE_DATA_16_BIT = 0x16;
- SERVICE_DATA_32_BIT = 0x20;
- SERVICE_DATA_128_BIT = 0x21;

GATT服务UUID:

- SERVICE_SOLICITATION_UUIDS_16_BIT = 0x14;
- SERVICE_SOLICITATION_UUIDS_32_BIT = 0x1F;
- SERVICE_SOLICITATION_UUIDS_128_BIT = 0x15;

厂商数据：

- MANUFACTURER_SPECIFIC_DATA = 0xFF;

广播数据的解析可参考：android.bluetooth.le.ScanRecord.parseFromBytes(byte[] scanRecord)

## Android BLE开发流程

### 1.添加必要权限

```
// 使用蓝牙必须声明此权限
<uses-permission android:name="android.permission.BLUETOOTH"/>
// 需要设置蓝牙时需要，如开关蓝牙
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
// API大于28时申请此权限
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
// API小于等于28时申请此权限
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
// API大于等于31时申请如下权限
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation"/>
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

### 2.获取BluetoothAdapter

```kotlin
// 如果返回adapter为null则设备不支持蓝牙
private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
    val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    bluetoothManager.adapter
}
```

### 3.启动蓝牙

```kotlin
// 判断蓝牙是否启动
if (!bluetoothAdapter.isEnabled) {
    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
}
```

### 4.扫描蓝牙设备

```kotlin
bluetoothAdapter.bluetoothLeScanner.startScan(object : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        val device: BluetoothDevice = result.device
        val scanRecord: ScanRecord? = result.scanRecord
        val rssi: Int = result.rssi
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
    }
})
```

### 5.连接蓝牙设备

```kotlin
val gattCallback = object : BluetoothGattCallback() {}
var bluetoothGatt: BluetoothGatt? = null
bluetoothGatt = device.connectGatt(this, false, gattCallback)
```

### 6.服务发现和初始化

```kotlin
val gattCallback = object : BluetoothGattCallback() {
    // 连接状态回调
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        when (newState) {
            // 在连接上设备后启动服务发现
            BluetoothProfile.STATE_CONNECTED -> {
                gatt.discoverServices()
            }
            BluetoothProfile.STATE_DISCONNECTED -> {

            }
        }
    }

    // 服务发现回调
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        val serviceUUID = "00002001-0000-1000-8000-00805f9b34fb"
        val service = gatt.getService(UUID.fromString(serviceUUID))
        val rxCharUUID = "00002002-0000-1000-8000-00805f9b34fb"
        val rxChar = service.getCharacteristic(UUID.fromString(rxCharUUID))
        val txCharUUID = "00002003-0000-1000-8000-00805f9b34fb"
        val txChar = service.getCharacteristic(UUID.fromString(txCharUUID))

        // 启用通知
        if ((txChar.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            val cccd = "00002902-0000-1000-8000-00805f9b34fb"
            val descriptor = txChar.getDescriptor(UUID.fromString(cccd))
            gatt.setCharacteristicNotification(txChar, true)
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }
    }

    // MTU改变回调
    override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
        super.onMtuChanged(gatt, mtu, status)
    }

    // 描述符写入回调
    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
    }

    // 特征变化通知回调
    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        super.onCharacteristicChanged(gatt, characteristic)
    }

    // 特征写入回调
    override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicWrite(gatt, characteristic, status)
    }
}
```

### 7.写入特征值

```kotlin
// 完成必要的初始化后就可以写入特征数据了，这里写入数据的大小需要根据mtu进行调整，默认mtu为23，包含一个字节的操作码(op code)和两个字节的属性句柄(attribute handle)，所以有效数据为mtu-3
val writeType =
    BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
if ((rxChar.properties and writeType) != 0) {
    val mtu = 23
    gatt.requestMtu(mtu)
    // Write Request and Write Command require 3 bytes for handler and op code.
    // Write Signed requires 12 bytes, as the signature is sent.
    val maxLength: Int = mtu - 3
    rxChar.value = "Hello world!".toByteArray()
    gatt.writeCharacteristic(rxChar)
}
```

### 8.关闭连接
```kotlin
bluetoothGatt.close()
```

