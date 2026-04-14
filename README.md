# Spring Boot Simple Shop

基于 Spring Boot 的简易电商后端项目，逐步集成 MySQL、Redis、RabbitMQ 等中间件，用于练习常见后端业务设计与项目工程化能力。

## 项目简介

这是一个以电商业务为背景的练手项目，围绕用户、商品、购物车、订单等核心模块逐步迭代实现，并在开发过程中持续引入 Redis 缓存、Redis 购物车、RabbitMQ 延迟消息等常见中间件能力。

## 技术栈

- Java
- Spring Boot
- MyBatis
- MySQL
- Redis
- RabbitMQ
- Maven

## 当前已实现功能

### 用户模块
- 用户 CRUD
- 参数校验

### 商品模块
- 商品 CRUD
- 商品详情缓存
- 商品列表缓存
- 空值缓存防穿透
- 商品新增/更新/删除时删除缓存

### 购物车模块
- 数据库购物车
- Redis 购物车
- 修改购物车数量
- 删除购物车商品
- Redis 购物车过期时间续期

### 订单模块
- 提交订单
- Redis 购物车下单
- 查询用户订单
- 查询订单详情
- 按状态查询订单
- 修改订单状态
- 取消订单
- 取消订单后恢复库存

### 异常处理
- 统一返回结果封装
- 全局异常处理
- 业务异常处理

### 消息队列
- 接入 RabbitMQ
- 使用 TTL + 死信队列实现延迟消息
- 下单成功后发送延迟取消订单消息
- 监听释放队列，自动取消超时未支付订单
- 已支付订单不会被误取消

## 当前项目亮点

- 使用 Redis 实现商品详情缓存与商品列表缓存
- 通过空值缓存减少缓存穿透问题
- 使用 Redis 存储购物车，并实现购物车过期时间自动续期
- 订单提交与取消流程中加入事务控制
- 基于 RabbitMQ 实现超时未支付订单自动取消
- 消费者收到延迟消息后再次校验订单状态，避免误取消已支付订单

## 项目结构说明

- `controller`：接口层
- `service`：业务层
- `mapper`：数据访问层
- `entity`：实体类
- `config`：配置类
- `listener`：消息监听器
- `constant`：常量类
- `mq.message`：消息对象

## 后续规划

- 接入 Swagger / OpenAPI 接口文档
- 增加 JWT 登录鉴权
- 防超卖优化
- 模拟支付流程
- 操作日志
- 单元测试
- Docker 部署

## 启动说明

### 环境要求
- JDK 17（或与你本地项目一致的版本）
- MySQL
- Redis
- RabbitMQ

### 启动步骤
1. 启动 MySQL，并创建项目数据库
2. 启动 Redis
3. 启动 RabbitMQ
4. 修改 `application.yml` 或 `application.properties` 中的数据库、Redis、RabbitMQ 配置
5. 启动 Spring Boot 项目

## 更新日志

详细迭代记录见 `CHANGELOG.md`
