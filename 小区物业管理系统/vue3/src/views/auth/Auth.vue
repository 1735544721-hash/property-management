<template>
  <div class="auth-container">
    <div class="auth-content">
      <div class="auth-left">
        <div class="auth-illustration">
          <img src="@/assets/宠物插画.svg" alt="宠物之家" />
        </div>
        <div class="auth-text">
          <h2>欢迎来到宠物之家</h2>
          <p>您的爱宠，我们的责任</p>
        </div>
      </div>
      
      <div class="auth-box">
        <div class="auth-header" v-if="showHeader">
          <div class="logo">🐾</div>
          <h1 class="title">宠物之家</h1>
          <div class="subtitle">PET HOME</div>
        </div>
        
        <el-form :model="formData" :rules="rules" ref="formRef" class="auth-form">
          <slot name="form-items"></slot>
          
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleSubmit" class="auth-button">
              {{ submitText }}
            </el-button>
          </el-form-item>
          
          <div class="auth-links">
            <slot name="auth-links"></slot>
          </div>
        </el-form>
      </div>
    </div>
    
    <div class="auth-decorations">
      <div class="auth-bubble bubble-1"></div>
      <div class="auth-bubble bubble-2"></div>
      <div class="auth-bubble bubble-3"></div>
      <div class="auth-bubble bubble-4"></div>
      <div class="auth-paw paw-1">🐾</div>
      <div class="auth-paw paw-2">🐾</div>
      <div class="auth-paw paw-3">🐾</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  formData: {
    type: Object,
    required: true
  },
  rules: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  submitText: {
    type: String,
    default: '提交'
  },
  showHeader: {
    type: Boolean,
    default: true
  }
})

const formRef = ref(null)

const emit = defineEmits(['submit'])

const handleSubmit = () => {
  formRef.value.validate(valid => {
    if (valid) {
      emit('submit', formRef)
    }
  })
}

defineExpose({
  formRef
})
</script>

<style lang="scss" scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #b29860 0%, #ffd45c 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.15);
    z-index: 1;
  }
}

.auth-content {
  display: flex;
  width: 100%;
  max-width: 1000px;
  position: relative;
  z-index: 2;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  border-radius: 16px;
  overflow: hidden;
}

.auth-left {
  width: 50%;
  background: linear-gradient(135deg, #FFB6C1 0%, #FFEE93 100%);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  
  .auth-illustration {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    
    img {
      max-width: 100%;
      max-height: 300px;
      object-fit: contain;
    }
  }
  
  .auth-text {
    text-align: center;
    padding: 20px 0;
    
    h2 {
      font-family: 'Nunito Sans', sans-serif;
      font-size: 28px;
      color: #683e35;
      margin-bottom: 10px;
    }
    
    p {
      font-family: 'Open Sans', sans-serif;
      font-size: 16px;
      color: #683e35;
      opacity: 0.8;
    }
  }
}

.auth-box {
  width: 50%;
  background: #FFF9E6;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;

  .logo {
    font-size: 40px;
    margin-bottom: 16px;
    background: #FFEE93;
    width: 70px;
    height: 70px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 16px;
    box-shadow: 0 4px 12px rgba(255, 182, 193, 0.3);
    animation: bounce 2s infinite;
  }

  .title {
    font-family: 'Nunito Sans', sans-serif;
    font-size: 28px;
    color: #683e35;
    margin: 0 0 8px;
  }

  .subtitle {
    font-family: 'Open Sans', sans-serif;
    font-size: 14px;
    color: #683e35;
    letter-spacing: 2px;
    opacity: 0.7;
  }
}

.auth-form {
  :deep(.el-input) {
    --el-input-hover-border-color: #FFB6C1;
    --el-input-focus-border-color: #FFB6C1;
    
    .el-input__wrapper {
      border-radius: 8px;
      transition: all 0.3s ease;
      background: rgba(255, 255, 255, 0.8);
      
      &.is-focus {
        box-shadow: 0 0 0 1px #FFB6C1;
        background: #fff;
      }

      &:hover {
        background: #fff;
      }
    }
  }
  
  .auth-button {
    width: 100%;
    height: 50px;
    border-radius: 8px;
    background: #FFA726;
    border: none;
    font-size: 18px;
    font-weight: 600;
    transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    transform: translateZ(0);
    backface-visibility: hidden;
    will-change: transform, box-shadow;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, #FFA726 0%, #FFD700 100%);
      opacity: 0;
      transition: opacity 0.3s ease;
      z-index: -1;
    }
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(255, 167, 38, 0.3);
      
      &::before {
        opacity: 1;
      }
    }
    
    &:active {
      transform: translateY(0);
    }
  }
}

.auth-links {
  margin-top: 20px;
  text-align: center;
  font-family: 'Open Sans', sans-serif;
  
  a {
    color: #FFA726;
    text-decoration: none;
    margin: 0 8px;
    transition: all 0.3s ease;
    
    &:hover {
      color: #FFB6C1;
      text-shadow: 0 2px 4px rgba(255, 182, 193, 0.3);
    }
  }
}

.auth-decorations {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 1;
  overflow: hidden;
  pointer-events: none;
}

.auth-bubble {
  position: absolute;
  border-radius: 50%;
  opacity: 0.2;
  background: #FFB6C1;
  
  &.bubble-1 {
    width: 150px;
    height: 150px;
    top: 10%;
    left: 15%;
    animation: float 8s ease-in-out infinite;
  }
  
  &.bubble-2 {
    width: 80px;
    height: 80px;
    top: 70%;
    left: 5%;
    animation: float 9s ease-in-out infinite 1s;
  }
  
  &.bubble-3 {
    width: 100px;
    height: 100px;
    top: 30%;
    right: 10%;
    animation: float 10s ease-in-out infinite 0.5s;
  }
  
  &.bubble-4 {
    width: 60px;
    height: 60px;
    bottom: 20%;
    right: 20%;
    animation: float 7s ease-in-out infinite 1.5s;
  }
}

.auth-paw {
  position: absolute;
  font-size: 24px;
  opacity: 0.3;
  
  &.paw-1 {
    top: 20%;
    right: 30%;
    animation: rotate 15s linear infinite;
  }
  
  &.paw-2 {
    bottom: 15%;
    left: 40%;
    animation: rotate 12s linear infinite reverse;
  }
  
  &.paw-3 {
    top: 50%;
    left: 15%;
    animation: rotate 20s linear infinite;
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .auth-content {
    flex-direction: column;
  }
  
  .auth-left,
  .auth-box {
    width: 100%;
  }
  
  .auth-left {
    padding: 30px 20px;
  }
  
  .auth-box {
    padding: 30px 20px;
  }
}
</style> 