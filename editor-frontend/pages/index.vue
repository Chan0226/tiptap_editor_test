<template>
  <div>
    <!-- 첫번째 TipTap 컴포넌트 -->
    <h3 class='tiptapTitle'>TipTap First</h3>
    <TipTap v-on:input='logText'/>
    
    <!-- 두번째 TipTap 컴포넌트 -->
    <h3 class='tiptapTitle'>TipTap Second</h3>
    <TipTap v-on:input='logText2'/>
  </div>
</template>

<script>
import TipTap from '../components/TipTap.vue'

export default {
  components: {
    TipTap
  },
  data() {
    return {
      editor1Content: '',
      editor2Content: ''
    }
  },
  methods: {
    logText(text) {
      // 에디터 1의 내용이 변경되었을 때 호출되는 콜백 함수
      console.log('Editor 1:', text);
    },
    logText2(text) {
      // 에디터 2의 내용이 변경되었을 때 호출되는 콜백 함수
      console.log('Editor 2:', text);
    }
  }
}
</script>
<style>
.tiptapTitle{
  text-align: center;
}
</style>
