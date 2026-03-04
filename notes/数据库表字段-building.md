# 楼栋表（building）字段清单

来源：`property-management/小区物业管理系统/sql/init.sql`

| 编号 | 字段名 | 类型 | 长度 | 是否非空 | 是否主键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | id | bigint | — | 是 | 是 | 主键ID |
| 2 | building_name | varchar | 50 | 是 | 否 | 楼栋名称 |
| 3 | floors | int | — | 是 | 否 | 楼层数 |
| 4 | units | int | — | 是 | 否 | 单元数 |
| 5 | description | varchar | 255 | 否 | 否 | 描述 |
| 6 | create_time | datetime | — | 否 | 否 | 创建时间 |
| 7 | update_time | datetime | — | 否 | 否 | 更新时间 |
| 8 | deleted | tinyint | — | 否 | 否 | 逻辑删除 |
