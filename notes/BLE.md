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
BLE广播包分为两种：Advertising Data广播包和Scan Response响应包
当主机主动扫描时会发送扫描请求给从机，从机收到请求后会返回响应包数据。

BLE广播包数据长度为31字节，有效数据长度不足时会补充0填满，有效数据部分包含多个广播数据单元称为AD Structure，AD Structure由Len和Data组成，第一个字节为Len表示Data的长度，Data的第一个字节为AD Type表示数据的类型，后面的Len-1个字节为AD Data。

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


