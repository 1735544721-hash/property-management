# 费用表（fee）字段清单

来源：`property-management/小区物业管理系统/sql/init.sql`

| 编号 | 字段名 | 类型 | 长度 | 是否非空 | 是否主键 | 注释 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | id | bigint | — | 是 | 是 | 主键ID |
| 2 | house_id | bigint | — | 是 | 否 | 房屋ID |
| 3 | fee_type | varchar | 20 | 是 | 否 | 费用类型（物业费/水费/电费/停车费） |
| 4 | amount | decimal | 10,2 | 是 | 否 | 金额 |
| 5 | fee_month | varchar | 10 | 是 | 否 | 费用月份（如2024-01） |
| 6 | status | tinyint | — | 否 | 否 | 状态（0未缴/1已缴） |
| 7 | pay_time | datetime | — | 否 | 否 | 缴费时间 |
| 8 | create_time | datetime | — | 否 | 否 | 创建时间 |
| 9 | update_time | datetime | — | 否 | 否 | 更新时间 |
| 10 | deleted | tinyint | — | 否 | 否 | 逻辑删除 |
