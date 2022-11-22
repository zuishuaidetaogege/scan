<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" :labelCol="{span: 6}" :wrapperCol="{span: 18}">
        <a-row :gutter="18">
          <a-col :md="8" :sm="24">
            <a-form-item label="PANELID" >
              <a-input @keyup.enter.native="onSearch" v-model="queryParam.panelid" placeholder="" allowClear/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="BOXID" >
              <a-input @keyup.enter.native="onSearch" v-model="queryParam.boxid" placeholder="" allowClear/>
            </a-form-item>
          </a-col>
          <template v-if="advanced">
            <a-col :md="8" :sm="24">
              <a-form-item label="PALLETID" >
                <a-input @keyup.enter.native="onSearch" v-model="queryParam.palletid" placeholder="" allowClear/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="!advanced && 8 || 24" :sm="24">
            <span class="table-page-search-submitButtons" :style="advanced && { float: 'right', overflow: 'hidden' } || {} ">
              <a-button type="primary" @click="onSearch">查询</a-button>
              <a-button @click="reset">重置</a-button>
              <a @click="toggleAdvanced">
                {{ advanced ? '收起' : '展开' }}
                <a-icon :type="advanced ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="default"
      :columns="columns"
      :dataSource="data"
      :pagination="pagination"
      :loading="loadingData"
      @change="handleTableChange"
      rowKey="id"
    >
      <span slot="action" slot-scope="">
        <span v-permission-missing="['detail', 'update', 'delete']">
          -
        </span>
      </span>
    </a-table>
  </a-card>
</template>

<script>
import list from '@/components/diboot/mixins/list'
export default {
  name: 'PalletinfoList',
  components: {
  },
  mixins: [list],
  data () {
    return {
      baseApi: '/palletinfo',
      getListFromMixin: true,
      columns: [
        {
          title: 'PALLETID',
          dataIndex: 'palletid'
        },
        {
          title: 'BOXID',
          dataIndex: 'boxid'
        },
        {
          title: 'PANELID',
          dataIndex: 'panelid'
        },
        {
          title: 'PRODUCTID',
          dataIndex: 'productid'
        },
        {
          title: 'STEPID',
          dataIndex: 'stepid'
        },
        {
          title: 'PRODTYPE',
          dataIndex: 'prodtype'
        },
        {
          title: 'STATUS',
          dataIndex: 'status'
        },
        {
          title: 'OPTIONCODE',
          dataIndex: 'optioncode'
        }
      ]
    }
  }

}
</script>
<style lang="less" scoped>
</style>
