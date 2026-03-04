# 房屋表（house）字段清单

来源：`property-management/小区物业管理系统/sql/init.sql`

| 编号 | 字段名 | 类型 | 长度 | 是否非空 | 是否主键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | id | bigint | — | 是 | 是 | 主键ID |
| 2 | building_id | bigint | — | 是 | 否 | 楼栋ID |
| 3 | unit_number | int | — | 是 | 否 | 单元号 |
| 4 | floor_number | int | — | 是 | 否 | 楼层号 |
| 5 | room_number | varchar | 20 | 是 | 否 | 房间号 |
| 6 | area | decimal | 10,2 | 否 | 否 | 面积(㎡) |
| 7 | owner_id | bigint | — | 否 | 否 | 业主ID |
| 8 | status | tinyint | — | 否 | 否 | 状态（0空置/1已入住） |
| 9 | create_time | datetime | — | 否 | 否 | 创建时间 |
| 10 | update_time | datetime | — | 否 | 否 | 更新时间 |
| 11 | deleted | tinyint | — | 否 | 否 | 逻辑删除 |
