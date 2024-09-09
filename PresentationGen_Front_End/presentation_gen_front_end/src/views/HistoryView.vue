<template>
  <div class="edit-history-container">
    <h2 class="title"><b>最近修改</b></h2>
    <div class="history-item">
      <el-empty :image-size="200" v-if="editHistory.length === 0"></el-empty>
      <el-card
        v-for="(historyItem, index) in editHistory"
        :key="index"
        shadow="hover"
        @click="openHistoryItem(historyItem.id)"
      >
        <img
          v-bind:src="historyItem.imageURL"
          :alt="historyItem.topic"
          class="thumbnail"
          @click="openHistoryItem(historyItem.id)"
        />
        <div class="info">
          <h4 class="historyTopic">{{ historyItem.topic }}</h4>
          <div class="timeAndDelete">
            <span class="time" style="font-size: small"
              >编辑时间:
              {{ formatDate(historyItem.updateTime) }} &nbsp;&nbsp;&nbsp;</span
            >
            <el-button @click="confirmDelete(historyItem.id)" size="mini"
              >删除</el-button
            >
          </div>
        </div>
      </el-card>
    </div>
    <div class="record-count">共查询到 {{ editHistory.length }} 项记录</div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      editHistory: []
    }
  },
  methods: {
    fetchEditHistory () {
      this.$axios
        .post('/ppt-record', null, {
          headers: {
            token: localStorage.getItem('token')
          }
        })
        .then((response) => {
          this.editHistory = response.data.data
          console.log(response.data)
        })
    },
    formatDate (isoDateString) {
      const date = new Date(isoDateString)
      const year = date.getFullYear()
      const month = ('0' + (date.getMonth() + 1)).slice(-2) // getMonth() 返回的是0-11，所以需要+1
      const day = ('0' + date.getDate()).slice(-2)
      const hours = ('0' + date.getHours()).slice(-2)
      const minutes = ('0' + date.getMinutes()).slice(-2)
      const seconds = ('0' + date.getSeconds()).slice(-2)
      const formattedDate = `${year}年${month}月${day}日 ${hours}:${minutes}:${seconds}`
      return formattedDate
    },
    confirmDelete (deleteID) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.deleteHistoryItem(deleteID)
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    deleteHistoryItem (deleteID) {
      this.$axios
        .post(
          '/delete-ppt',
          {
            id: deleteID
          },
          {
            headers: {
              token: localStorage.getItem('token')
            }
          }
        )
        .then((response) => {
          this.editHistory = this.editHistory.filter(
            (histItem) => histItem.id !== deleteID
          )
        })
    },
    openHistoryItem (ID) {
      this.$axios
        .post(
          '/load-ppt',
          {
            id: ID
          },
          {
            headers: {
              token: localStorage.getItem('token')
            }
          }
        )
        .then((response) => {
          this.$router.push({
            name: 'Edit',
            params: {
              markdownData: JSON.stringify(response.data.data.markdownData),
              templates: JSON.stringify(response.data.data.templates),
              id: JSON.stringify(ID)
            }
          })
        })
    }
  },
  mounted () {
    this.fetchEditHistory()
  }
}
</script>

<style scoped>
.title {
  color: black;
  margin-left: 30px;
  margin-top: 0px;
  margin-bottom: 40px;
  font-size: 30px;
}
.edit-history-container {
  margin: 50px;
}

.history-item {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 创建四列，每列宽度相等 */
  gap: 20px; /* 设置网格间隙 */
}

.el-card {
  background-color: rgb(243, 251, 255);
}

.thumbnail {
  width: 100%;
  height: auto;
}

.historyTopic {
  margin-bottom: 0px;
  font-size: 20px;
}

.timeAndDelete {
  display: flex;
  justify-content: space-between;
}

.record-count {
  color: gray;
  text-align: center; /* 在底部中间显示记录数 */
  margin-top: 20px; /* 与卡片间距 */
  font-size: 16px; /* 文字大小 */
  padding: 20px;
}
</style>
