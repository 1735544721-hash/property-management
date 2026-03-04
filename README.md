# property-management

小区物业管理系统相关材料与代码。

## 目录结构
- `小区物业管理系统/`：项目源码与数据库脚本
- `notes/`：数据库表与字段说明、写作素材
- `openclaw必读资料/`：写作与材料参考

## 快速开始
- 初始化数据库并导入 `小区物业管理系统/sql/init.sql`
- 按需更新 `小区物业管理系统/property-management-backend/src/main/resources/application.yml`
- 进入 `小区物业管理系统/property-management-backend`，运行 `mvn spring-boot:run`
- 后端地址：`http://localhost:8080/api`
- 进入 `小区物业管理系统/property-management-frontend`，执行 `npm install && npm run dev`
- 前端地址：`http://localhost:3000`
- 详细步骤见 `notes/运行清单.md`

## 数据库文档
- 表清单：`notes/数据库表清单.md`
- 字段说明索引：`notes/README.md`
