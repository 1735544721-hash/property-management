# 公告表（notice）字段清单

来源：`property-management/小区物业管理系统/sql/init.sql`

| 编号 | 字段名 | 类型 | 长度 | 是否非空 | 是否主键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | id | bigint | — | 是 | 是 | 主键ID |
| 2 | title | varchar | 100 | 是 | 否 | 公告标题 |
| 3 | content | text | — | 否 | 否 | 公告内容 |
| 4 | publisher_id | bigint | — | 否 | 否 | 发布人ID |
| 5 | status | tinyint | — | 否 | 否 | 状态（0草稿/1已发布） |
| 6 | create_time | datetime | — | 否 | 否 | 创建时间 |
| 7 | publish_time | datetime | — | 否 | 否 | 发布时间 |
| 8 | update_time | datetime | — | 否 | 否 | 更新时间 |
| 9 | deleted | tinyint | — | 否 | 否 | 逻辑删除 |
