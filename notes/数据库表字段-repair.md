# 报修表（repair）字段清单

来源：`property-management/小区物业管理系统/sql/init.sql`

| 编号 | 字段名 | 类型 | 长度 | 是否非空 | 是否主键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | id | bigint | — | 是 | 是 | 主键ID |
| 2 | user_id | bigint | — | 是 | 否 | 报修人ID |
| 3 | house_id | bigint | — | 否 | 否 | 房屋ID |
| 4 | title | varchar | 100 | 是 | 否 | 报修标题 |
| 5 | content | text | — | 否 | 否 | 报修内容 |
| 6 | images | varchar | 500 | 否 | 否 | 图片URL（多张用逗号分隔） |
| 7 | status | tinyint | — | 否 | 否 | 状态（0待处理/1处理中/2已完成） |
| 8 | handler_id | bigint | — | 否 | 否 | 处理人ID |
| 9 | handle_result | text | — | 否 | 否 | 处理结果 |
| 10 | create_time | datetime | — | 否 | 否 | 创建时间 |
| 11 | handle_time | datetime | — | 否 | 否 | 处理时间 |
| 12 | update_time | datetime | — | 否 | 否 | 更新时间 |
| 13 | deleted | tinyint | — | 否 | 否 | 逻辑删除 |
