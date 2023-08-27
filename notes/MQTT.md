# MQTT协议解析

MQTT(Message Queuing Telemetry Transport, 消息队列遥测传输协议)，是一种基于发布/订阅模式的轻量级通信协议，该协议构建于TCP/IP协议之上。

MQTT协议用极少的代码和有限的带宽，为远程连接设备提供实时可靠的消息服务。

### 报文格式

MQTT协议报文由三部分组成，固定报头、可变报头和有效载荷。

固定报头是每个报文都有的部分，可变报头要看固定报头中的报文类型来确定是否存在，有效载荷的内容也由报文类型确定。

### 固定报头

固定报头占用至少两个字节，第一个字节为报文类型和标志位，第2至N字节为剩余内容的长度，最多4字节；

剩余长度每个字节第7bit用于表示后面字节是否属于剩余长度字节，所以一个字节有效长度为127，超过127则第7bit置1，后面字节有效bit位拼接到前面；

第一个字节的[7, 4]bit为报文类型，[3, 0]bit为标志位，标志位根据报文类型来决定，PUBLISH报文中3bit为DUP表示是否重复消息，[2, 1]
bit为QOS服务质量，0bit为Retain表示是否保存消息，需要QOS大于1才会起作用。

#### 报文类型：

- RESERVED（0）：保留
- CONNECT（1）：客户端向服务端发送连接请求
- CONNACK（2）：服务端响应客服务连接请求
- PUBLISH（3）：发布消息
- PUBACK（4）：QOS1发布确认
- PUBREC（5）：回应发布收到，QOS2第一步
- PUBREL（6）：回复发布收到，在QOS2第二步
- PUBCOMP（7）：回复发布完成，在QOS2第三步
- SUBSCRIBE（8）：客户端请求订阅
- SUBACK（9）：服务端向客户端确认订阅请求
- UNSUBSCRIBE（10）：客户端请求取消订阅
- UNSUBACK（11）：服务端确认取消订阅请求
- PINGREQ（12）：客户端向服务端发送心跳请求
- PINGRESP（13）：服务端向客户端发送心跳响应
- DISCONNECT（14）：客户端向服务端断开连接
- RESERVED（15）：保留

### CONNECT报文

连接报文的固定报头类型为CONNECT，可变报文长度为10字节，内容包括：

- 2字节 协议名称长度
- 4字节 协议名称（MQTT）
- 1字节 协议版本
- 1字节 连接标志
- 2字节 KeepAlive

#### 连接标志位：

- 7bit username flag 用户名
- 6bit password flag 密码
- 5bit will retain 遗嘱保留
- 4-3bit will qos 遗嘱服务质量
- 2bit will flag 遗嘱
- 1bit clean session是否清除会话
- 0bit 保留

#### 有效载荷字段根据连接标志位决定是否存在，存在的字段按如下顺序出现，字段前2个字节为字段长度，后面跟着字段内容：

- Client Id 客户端标识
- Will Properties 遗嘱属性
- Will Topic 遗嘱主题
- Will Payload 遗嘱载荷
- UserName 用户名
- Password 密码

### PUBLISH报文

连接报文的固定报头类型为PUBLISH，可变报文包含：

- 主题名（前两字节为主题长度，后面跟着主题名）
- 2字节的报文标志符，只在Qos>0时才有

有效载荷：客户端发送的消息

### 服务质量

使用MQTT的设备运行在受限的网络环境下，只依靠TCP传输协议并不能完全保证消息的可靠性，因此MQTT提供了QOS机制，来满足不同场景下对消息可靠性的要求。

#### MQTT定义了3种QOS等级：

- QOS 0，最多交付一次
- QOS 1， 至少交付一次
- QOS 2，只交付一次

