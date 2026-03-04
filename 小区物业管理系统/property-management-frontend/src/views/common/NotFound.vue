<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.not-found {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1E3A5F 0%, #2E5A8F 100%);
  
  .content {
    text-align: center;
    
    h1 {
      font-size: 120px;
      font-weight: 700;
      color: #fff;
      text-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    }
    
    p {
      font-size: 20px;
      color: rgba(255, 255, 255, 0.8);
      margin: 20px 0 40px;
    }
  }
}
</style>

