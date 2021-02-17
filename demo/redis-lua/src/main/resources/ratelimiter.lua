-- 声明变量
local methodKey = KEYS[1]
print("key is "..methodKey)
-- redis控制台打印日志 注意配置文件配置等级 可去配置文件中logfile="" 路劲下查看
redis.log(redis.LOG_DEBUG, "key is ", methodKey)

-- 传入的限流值
print("value is "..ARGV[1])
local limit = tonumber(ARGV[1])
print("convert is "..limit)

-- 获取当前限流个数 可能为nil（key不存在）
local count = tonumber(redis.call("get", methodKey) or "0")
print("count is "..count)

if count + 1 > limit then
    -- 超出限流数  拒绝
    redis.log(redis.LOG_INFO, "limit result is ", false)
    return false
end

-- 自增1
redis.call('INCRBY',methodKey,1)
-- 设置过期时间 一秒
redis.call('EXPIRE',methodKey,1)

return true