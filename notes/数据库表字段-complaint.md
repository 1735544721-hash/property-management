# 投诉建议表（complaint）字段清单

来源：`property-management/小区物业管理系统/sql/init.sql`

| 编号 | 字段名 | 类型 | 长度 | 是否非空 | 是否主键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | id | bigint | — | 是 | 是 | 主键ID |
| 2 | user_id | bigint | — | 是 | 否 | 投诉人ID |
| 3 | type | varchar | 20 | 是 | 否 | 类型（投诉/建议） |
| 4 | title | varchar | 100 | 是 | 否 | 标题 |
| 5 | content | text | — | 否 | 否 | 内容 |
| 6 | status | tinyint | — | 否 | 否 | 状态（0待处理/1已处理） |
| 7 | reply | text | — | 否 | 否 | 回复内容 |
| 8 | handler_id | bigint | — | 否 | 否 | 处理人ID |
| 9 | create_time | datetime | — | 否 | 否 | 创建时间 |
| 10 | handle_time | datetime | — | 否 | 否 | 处理时间 |
| 11 | update_time | datetime | — | 否 | 否 | 更新时间 |
| 12 | deleted | tinyint | — | 否 | 否 | 逻辑删除 |
