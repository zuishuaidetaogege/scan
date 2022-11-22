<template>
  <global-footer class="footer custom-render">
    <template v-slot:links>
    </template>
    <template v-slot:copyright>
      苏州华星--制造革新G &copy; 2022 <a
        :href="goto"
        target="_blank"
      >{{ title }}</a>
    </template>
  </global-footer>
</template>

<script>
import { GlobalFooter } from '@ant-design-vue/pro-layout'

export default {
  name: 'ProGlobalFooter',
  data () {
    return {
      title: process.env.VUE_APP_SSO_PLATFORM,
      goto: process.env.VUE_APP_UNIFIY_PLATFORM_URL
    }
  },
  components: {
    GlobalFooter
  }
}
</script>
<style lang="less">
.ant-layout-footer {
  padding: 0;
  .ant-pro-global-footer {
    margin:15px 0;
  }
}
</style>
