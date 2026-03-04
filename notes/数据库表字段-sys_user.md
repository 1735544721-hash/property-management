# 用户表（sys_user）字段清单

来源：`property-management/小区物业管理系统/sql/init.sql`

| 编号 | 字段名 | 类型 | 长度 | 是否非空 | 是否主键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | id | bigint | — | 是 | 是 | 主键ID |
| 2 | username | varchar | 50 | 是 | 否 | 用户名 |
| 3 | password | varchar | 255 | 是 | 否 | 密码（加密） |
| 4 | real_name | varchar | 50 | 否 | 否 | 真实姓名 |
| 5 | phone | varchar | 20 | 否 | 否 | 手机号 |
| 6 | email | varchar | 100 | 否 | 否 | 邮箱 |
| 7 | avatar | varchar | 255 | 否 | 否 | 头像URL |
| 8 | role | varchar | 20 | 是 | 否 | 角色（admin/staff/resident） |
| 9 | status | tinyint | — | 是 | 否 | 状态（0禁用/1启用） |
| 10 | create_time | datetime | — | 否 | 否 | 创建时间 |
| 11 | update_time | datetime | — | 否 | 否 | 更新时间 |
| 12 | deleted | tinyint | — | 否 | 否 | 逻辑删除（0未删除/1已删除） |
