<template>
  <el-container style="height: 100%; overflow-y: hidden;">
    <div class="fixed-header">
      <div style="width: 100%; display: flex; align-items: center;">
        <el-button @click="back" type="text"><i class="el-icon-back"></i>
          <span style="font-weight: bold;">返回</span>
        </el-button>
        <div style="
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: flex-end;
          ">
          <el-button @click="restartIntro" el-button type="primary" size="small" round>教程指引&nbsp;<i
              class="el-icon-help el-icon--right"></i></el-button>
          <el-button @click="save" el-button type="primary" size="small" round>保存更改&nbsp;<i
              class="el-icon-upload el-icon--right"></i></el-button>
          <el-button @click="generate" el-button type="primary" size="small" round>保存并生成PPT&nbsp;<i
              class="el-icon-s-opportunity"></i></el-button>
          <el-button @click="downloadFile" :disabled="downloadUrl === ''" el-button type="primary" size="small"
            round>下载&nbsp;<i class="el-icon-download"></i></el-button>
        </div>
      </div>
    </div>
    <el-main style="padding-top: 8px; margin-top: 47px;border: 1px dashed #ccc;">
      <div class="content">
        <el-card shadow="hover" style="border-radius: 80px; border-width: 5px" data-step="8" data-intro="点击编辑文本内容">
          <el-row>
            <el-col :span="3" offset="1">
              <span style="font-size: medium; color: grey; font-style: italic"><br />演示标题：</span>
            </el-col>
            <el-col :span="19">
              <el-input class="text" style="font-size: x-large"
                v-model="markdownData.parseMarkdownText[0].key"></el-input>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="3" offset="1">
              <span style="font-size: medium; color: grey; font-style: italic"><br />副标题：</span>
            </el-col>
            <el-col :span="6">
              <el-input class="text" style="font-size: medium"
                v-model="markdownData.parseMarkdownText[0].values[0]"></el-input>
            </el-col>
            <el-col :span="4" offset="1">
              <span style="font-size: medium; color: grey; font-style: italic"><br />配图关键词：</span>
            </el-col>
            <el-col :span="8">
              <el-input class="text" style="font-size: medium" v-model="markdownData.mainObject"></el-input>
            </el-col>
          </el-row>
        </el-card>
        <br />
        <br />
        <div>
          <div v-for="(
              chapter, chapterIndex
            ) in markdownData.parseMarkdownText.slice(1)" :key="chapterIndex" class="chapter">
            <el-row>
              <el-tag type="info" closable @close="deleteChapter(chapterIndex)" style="
                  font-size: x-large;
                  font-weight: 600;
                  font-style: italic;
                  margin-bottom: 20px;
                " :data-step="chapterIndex === 0 ? '4' : ''" :data-intro="chapterIndex === 0 ? '点击删除章节' : ''">第{{
                chapterIndex + 1 }}章&nbsp;&nbsp;</el-tag>
            </el-row>
            <el-row>
              <el-col :span="10"><el-input v-model="chapter.key" class="chapterTitle">
                  <span slot="prefix">章节标题</span>
                </el-input>
                <!-- 章节标题 -->
              </el-col>
              <el-col :span="8" offset="1"><el-input v-model="chapter.values[0]" class="chapterSideTitle">
                  <span slot="prefix">章节副标题</span>
                </el-input>
                <!-- 章节副标题 -->
              </el-col>
            </el-row>
            <!-- 跳过章节副标题，从索引1开始，并且步长为4 -->
            <div v-for="sectionIndex in getSectionIndexes(chapter.values.length)" :key="sectionIndex"
              class="section-pair" @click="
                selectedChapter = chapterIndex + 1
              selectedSection = (sectionIndex + 1) / 2
              selectedIs2 = sectionIndex + 3 < chapter.values.length
                "
                :data-step="chapterIndex === 0 &&sectionIndex===1? '2' : ''" data-intro="蓝线内小节将在一页幻灯片内展示,点击蓝线右方区域开始选择本页模板"
                >
              <span v-if="sectionIndex === 1 && chapterIndex === 0" style="
                  font-size: small;
                  font-style: italic;
                  margin-left: 20px;
                  margin-right: 0px;
                  color: #409eff;
                ">
                蓝线内小节将在一页幻灯片内展示<br>
              </span>
              <el-tag closable @close="deleteSection(chapterIndex, sectionIndex)" style="
                    font-weight: 600;
                    font-style: italic;
                    margin-left: 20px;
                    margin-right: 0px;
                  "
                  :data-step="chapterIndex === 0 &&sectionIndex===1? '6' : ''" data-intro="点击删除小节"

                  >第{{ (sectionIndex + 1) / 2 }}节&nbsp;&nbsp;</el-tag>
              <el-input v-model="chapter.values[sectionIndex]" class="sectionTitle">
              </el-input>
              <!-- 小节标题 -->
              <el-tag @click="addSection(chapterIndex, sectionIndex)" style="
                    font-weight: 600;
                    font-style: italic;
                    margin-left: 20px;
                    margin-right: 0px;
                  "
                  :data-step="chapterIndex === 0 &&sectionIndex===1? '7' : ''" data-intro="点击在本节后添加小节"
                  >在本节后添加小节&nbsp;&nbsp;</el-tag>
              <el-input type="textarea" v-model="chapter.values[sectionIndex + 1]" autosize="{minRows:2,maxRows:6}"
                class="sectionBody">
              </el-input>
              <!-- 小节内容 -->
              <!-- 检查是否有第二个小节存在 -->
              <div v-if="sectionIndex + 3 < chapter.values.length">
                <el-tag closable @close="deleteSection(chapterIndex, sectionIndex + 2)" style="
                    font-weight: 600;
                    font-style: italic;
                    margin-left: 10px;
                    margin-right: 0px;
                  ">第{{ (sectionIndex + 3) / 2 }}节&nbsp;&nbsp;</el-tag>
                <el-input v-model="chapter.values[sectionIndex + 2]" class="sectionTitle"></el-input>
                <el-tag @click="addSection(chapterIndex, sectionIndex + 2)" style="
                    font-weight: 600;
                    font-style: italic;
                    margin-left: 10px;
                    margin-right: 0px;
                  ">在本节后添加小节&nbsp;&nbsp;</el-tag>
                <!-- 下一个小节标题 -->
                <el-input type="textarea" v-model="chapter.values[sectionIndex + 3]" autosize="{minRows:2,maxRows:6}"
                  class="sectionBody"></el-input>
                <!-- 下一个小节内容 -->
              </div>
            </div>
            <el-tag type="info" @click="addChapter(chapterIndex)" style="
                font-size: small;
                margin-bottom: 30px;
                margin-left: 20px;
                bottom: 3px;
              "
              :data-step="chapterIndex === 0 ? '5' : ''" data-intro="点击在本章后添加章节"
              >点击在本章后添加章节&nbsp;&nbsp;</el-tag>
          </div>
        </div>
      </div>
    </el-main>
    <el-aside :width="widthData.width + 'px'" id="asideId" style="
        display: flex;
        flex-direction: column;
        overflow: hidden;
        background-color: #f2f2f2;
        height: 100vh;
        margin-top: 47px
      ">
      <div class="PPTPreview" v-loading="isGenerating" element-loading-text="全力生成中"
        element-loading-background="rgba(0, 0, 0, 0.2)">
        <el-empty v-if="downloadUrl === '' && !isGenerating" description="请先保存并生成PPT"></el-empty>
        <div v-show="downloadUrl !== ''" style="
            position: relative;
            width: 100%;
            aspect-ratio: 16 / 9; /* 直接指定宽高比为16:9 */
          ">
          <iframe id="officeViewer" src="" width="100%" height="100%" frameborder="0"> </iframe>
        </div>
      </div>
      <div class="templatesSelector" style="flex: 1; overflow-y: auto">
        <el-tabs type="border-card" v-model="activeTab">
          <el-tab-pane name="ppt">
            <span slot="label" data-step="1" data-intro="在此栏切换PPT模板"><i class="el-icon-picture"></i> PPT模板</span>
            <span style="font-size: small; color: cornflowerblue">筛选&nbsp;:&nbsp;&nbsp;</span>
            <el-select v-model="color" placeholder="请选择颜色" size="mini" clearable style="width: 120px">
              <el-option v-for="item in colors" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
            <el-select clearable v-model="Style" placeholder="请选择风格" size="mini"
              style="width: 120px; margin-left: 20px">
              <el-option v-for="item in Styles" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
            <div class="image-container">
              <el-row :gutter="10">
                <el-col :span="8" v-for="item in PPTTemplates" :key="item.id">
                  <el-card class="image-card" :style="{
                    backgroundColor:
                      selectedPPTTemplateID === item.id
                        ? '#728af7'
                        : '#f2f2f2',
                  }" shadow="hover">
                    <img :src="item.image" class="image" @click="
                      selectedPPTTemplateID = item.id;
                    selectedPPTTemplateColor = item.colour;
                    templatesStructure[0][0] = item.id;
                    queryChapterTemplates();
                    " />
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>
          <el-tab-pane name="chapter">
            <span slot="label" data-step="3" data-intro="在此栏选择页面模板，选择有配图模板会根据关键词配图"><i class="el-icon-s-fold"></i>
              页面模板</span>
            <span style="font-size: small; color: cornflowerblue">当前操作的幻灯片是&nbsp;:&nbsp;&nbsp;</span>
            <span style="font-size: small; color: cornflowerblue; font-weight: bold">第{{ selectedChapter }}章：{{
              selectedSectionString }}</span>
            <span style="
                font-size: medium;
                color: cornflowerblue;
                font-weight: bold;
              "><br /><br />无配图模板:</span>
            <div class="image-container">
              <el-row :gutter="10">
                <el-col :span="8" v-for="item in chapterTemplatesNoPic" :key="item.id">
                  <el-card class="image-card" :style="{
                    backgroundColor:
                      selectedChapterTemplateID === item.id
                        ? '#728af7'
                        : '#f2f2f2',
                  }" shadow="hover">
                    <img :src="item.image" class="image" @click="selectChapterTemplate(item.id)" />
                  </el-card>
                </el-col>
              </el-row>
            </div>
            <span style="
                font-size: medium;
                color: cornflowerblue;
                font-weight: bold;
              "><br /><br />有配图模板:</span>
            <div class="image-container">
              <el-row :gutter="10">
                <el-col :span="8" v-for="item in chapterTemplatesHasPic" :key="item.id">
                  <el-card class="image-card" :style="{
                    backgroundColor:
                      selectedChapterTemplateID === item.id
                        ? '#728af7'
                        : '#f2f2f2',
                  }" shadow="hover">
                    <img :src="item.image" class="image" @click="selectChapterTemplate(item.id)" />
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-aside>
  </el-container>
</template>

<script>
import introJs from 'intro.js'
import 'intro.js/introjs.css'
export default {
  data () {
    return {
      id: 18,
      markdownData: {},
      templates: [],
      templatesStructure: [],
      widthData: {
        width: 600 // 初始宽度
      },
      downloadUrl: '',
      selectedPPTTemplateID: '',
      selectedChapterTemplateID: '',
      selectedPPTTemplateColor: '',
      activeTab: 'ppt',
      color: '',
      Style: '',
      intro: '',
      isGenerating: false,
      selectedChapter: 0,
      selectedSection: 0,
      selectedIs2: true,
      token: localStorage.getItem('token'),
      colors: [
        {
          value: '红色',
          label: '红色'
        },
        {
          value: '橙色',
          label: '橙色'
        },
        {
          value: '蓝色',
          label: '蓝色'
        },
        {
          value: '黄色',
          label: '黄色'
        },
        {
          value: '绿色',
          label: '绿色'
        },
        {
          value: '粉色',
          label: '粉色'
        },
        {
          value: '紫色',
          label: '紫色'
        },
        {
          value: '黑色',
          label: '黑色'
        },
        {
          value: '白色',
          label: '白色'
        }
      ],
      Styles: [
        {
          value: '插画',
          label: '插画'
        },
        {
          value: '简约',
          label: '简约'
        },
        {
          value: '科技',
          label: '科技'
        },
        {
          value: '可爱',
          label: '可爱'
        },
        {
          value: '国风',
          label: '国风'
        },
        {
          value: '风景',
          label: '风景'
        }
      ],
      PPTTemplates: [],
      chapterTemplatesHasPic: [],
      chapterTemplatesNoPic: []
    }
  },
  computed: {
    selectedSectionString: function () {
      return (
        ' 第' +
        this.selectedSection +
        '节' +
        (this.selectedIs2 ? ' 第' + (this.selectedSection + 1) + '节' : '')
      )
    },
    watchBoth () {
      return [this.selectedChapter, this.selectedSection]
    }
  },
  methods: {
    updateWidth () {
      const resize = document.getElementById('asideId')
      resize.onmousedown = (e) => {
        let startX = e.clientX // 初始点击位置
        document.onmousemove = (e) => {
          const endX = e.clientX // 鼠标移动后的位置
          const moveLen = startX - endX // 计算移动长度
          startX = endX // 更新当前鼠标位置

          // 更新侧边栏宽度
          this.widthData.width += moveLen

          // 限制侧边栏的最小和最大宽度
          if (this.widthData.width < 20) {
            this.widthData.width = 20 // 最小宽度
          } else if (this.widthData.width > 1200) {
            this.widthData.width = 1200 // 最大宽度
          }
        }
        document.onmouseup = () => {
          document.onmousemove = null // 停止移动事件
          document.onmouseup = null // 停止鼠标松开事件
        }
        return false // 阻止默认事件和冒泡
      }
    },
    selectChapterTemplate (id) {
      this.templatesStructure[this.selectedChapter][
        parseInt(this.selectedSection / 2)
      ] = id
      this.selectedChapterTemplateID = id
    },
    parseTemplates () {
      this.templatesStructure[0] = []
      this.templatesStructure[0][0] = this.templates[0]
      let index = 1
      console.log(this.markdownData.parseMarkdownText)
      for (let i = 1; i < this.markdownData.parseMarkdownText.length; i++) {
        this.templatesStructure[i] = []
        for (
          let j = 0;
          j <
          parseInt(
            (this.markdownData.parseMarkdownText[i].values.length - 1) / 4
          );
          j++
        ) {
          this.templatesStructure[i][j] = this.templates[index++]
        }
      }
      console.log('解析后的模板')
      console.log(this.templatesStructure)
    },
    flatTemplates () {
      this.templates = this.templatesStructure.flat()
      const zeroIndices = []
      this.templates.forEach((element, index) => {
        if (element === 0) {
          zeroIndices.push(index - 1)
        }
      })
      return zeroIndices
    },
    switchTab (tabName) {
      this.activeTab = tabName // Set the active tab
    },
    queryPPTTemplates () {
      this.$axios
        .post('/ppt-template', {
          colour: this.color,
          tag: this.Style
        })
        .then((response) => {
          this.PPTTemplates = response.data.data
          response.data.data.forEach((template) => {
            if (template.id === this.selectedPPTTemplateID) {
              this.selectedPPTTemplateColor = template.colour
              this.queryChapterTemplates()
            }
          })
        })
    },
    queryChapterTemplates () {
      const point = this.selectedIs2 ? 2 : 1
      this.$axios
        .post(
          '/chapter-template',
          {
            colour: this.selectedPPTTemplateColor,
            point: point,
            hasPicture: true
          },
          {
            headers: {
              token: this.token,
              'Content-Type': 'application/json'
            }
          }
        )
        .then((response) => {
          this.chapterTemplatesHasPic = response.data.data
        })
      this.$axios
        .post(
          '/chapter-template',
          {
            colour: this.selectedPPTTemplateColor,
            point: point,
            hasPicture: false
          },
          {
            headers: {
              token: this.token,
              'Content-Type': 'application/json'
            }
          }
        )
        .then((response) => {
          this.chapterTemplatesNoPic = response.data.data
        })
    },
    getSectionIndexes (length) {
      return Array.from({ length: length - 1 }, (_, i) => 1 + i * 4).filter(
        (index) => index < length
      )
    },
    addSection (chapterIndex, sectionIndex) {
      if (
        (this.markdownData.parseMarkdownText[chapterIndex + 1].values.length -
          1) /
        2 ===
        6
      ) {
        this.$message({
          message: '一章的小节不能多过6个',
          type: 'error',
          showClose: true
        })
        return
      }
      this.$message(
        '在第' +
        (chapterIndex + 1) +
        '章第' +
        ((sectionIndex - 1) / 2 + 1) +
        '节后添加小节'
      )
      // 更改模板数组
      if (
        ((this.markdownData.parseMarkdownText[chapterIndex + 1].values.length -
          1) /
          2) %
        2 ===
        0
      ) {
        this.templatesStructure[chapterIndex + 1].push(0)
      }
      const values =
        this.markdownData.parseMarkdownText[chapterIndex + 1].values
      values.splice(sectionIndex + 2, 0, '新建小节标题', '新建小节内容')
    },
    deleteChapter (chapterIndex) {
      if (this.markdownData.parseMarkdownText.length - 1 === 2) {
        this.$message({
          message: '章节数量不能少于2个',
          type: 'error'
        })
        return
      }
      this.templatesStructure.splice(chapterIndex + 1, 1)
      // 使用 splice 来移除选中的章节
      this.markdownData.parseMarkdownText.splice(chapterIndex + 1, 1)
    },
    deleteSection (chapterIndex, sectionIndex) {
      if (
        (this.markdownData.parseMarkdownText[chapterIndex + 1].values.length -
          1) /
        2 ===
        2
      ) {
        this.$message({
          message: '一章的小节不能少于2个',
          type: 'error'
        })
        return
      }
      // 更改模板数组
      if (
        ((this.markdownData.parseMarkdownText[chapterIndex + 1].values.length -
          1) /
          2) %
        2 ===
        1
      ) {
        this.templatesStructure[chapterIndex + 1].pop()
      }
      this.$message(
        '删除第' +
        (chapterIndex + 1) +
        '章第' +
        ((sectionIndex - 1) / 2 + 1) +
        '节'
      )
      console.log()
      this.markdownData.parseMarkdownText[chapterIndex + 1].values.splice(
        sectionIndex,
        2
      )
    },
    addChapter (chapterIndex) {
      if (this.markdownData.parseMarkdownText.length - 1 === 6) {
        this.$message({
          message: '章节数量不能超过6个',
          type: 'error'
        })
        return
      }
      // 创建一个新的章节对象，包含两个小节
      const newChapter = {
        key: '新建章节标题',
        values: [
          '新建章节副标题',
          '新建小节标题1',
          '新建小节内容1',
          '新建小节标题2',
          '新建小节内容2'
        ]
      }
      this.templatesStructure.splice(chapterIndex + 2, 0, [0])
      // 使用 splice 在指定章节后插入新章节
      // chapterIndex + 1 是插入点，0 表示不删除任何现有元素，newChapter 是要插入的新章节
      this.markdownData.parseMarkdownText.splice(
        chapterIndex + 2,
        0,
        newChapter
      )
    },
    back () {
      this.$confirm('未保存的操作将丢失, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$router.push({ name: 'History' })
        })
        .catch(() => { })
    },
    save () {
      const zeroIndices = this.flatTemplates()
      if (zeroIndices.length === 0) {
        this.$message({
          type: 'success',
          message: '保存成功'
        })
      } else {
        const zeroIndicesString = zeroIndices.join(', ')
        this.$message.error({ message: '请选择完每一章的模板后再保存，' + `未选择模板的页面有 (${zeroIndicesString})`, showClose: 'true' })
        return 0
      }
      this.$axios
        .post(
          '/save-ppt',
          {
            id: this.id,
            markdownData: this.markdownData,
            templates: this.templates
          },
          {
            headers: {
              token: this.token,
              'Content-Type': 'application/json'
            }
          }
        )
        .then((response) => {
          console.log(response.data.msg)
        })
      // Logic to submit data to the backend
    },
    generate () {
      if (this.save() === 0) {
        return
      }
      this.isGenerating = true
      this.$axios({
        method: 'post',
        url: '/gen-ppt',
        data: {
          markdownData: this.markdownData,
          templates: this.templates
        },
        headers: {
          token: this.token,
          'Content-Type': 'application/json'
        },
        timeout: 60000
      })
        .then((response) => {
          this.downloadUrl = response.data.data
          console.log(this.downloadUrl)
          // 可以立即触发下载，或者让用户点击另一个按钮来下载
        })
        .catch((error) => {
          console.error('Error downloading the file:', error)
          this.$message({
            message: '图片下载失败，请重试',
            type: 'error',
            showClose: true
          })
        })
        .finally(() => {
          this.isGenerating = false // 结束下载
          if (this.downloadUrl.length > 1) {
            const iframe = document.getElementById('officeViewer')
            const src = 'https://view.officeapps.live.com/op/embed.aspx?src=' + encodeURIComponent(this.downloadUrl) + '&wdAr=1.7777777777777777&wdEaa=0'
            iframe.src = src
          }
        })
    },
    downloadFile () {
      const link = document.createElement('a')
      link.href = this.downloadUrl
      link.setAttribute(
        'download',
        this.markdownData.parseMarkdownText[0].key + '.pptx'
      )
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },
    jumpSlide (chapter, section) {
      this.switchTab('chapter')
      this.queryChapterTemplates()
      this.selectedChapterTemplateID =
        this.templatesStructure[chapter][(section + 1) / 2 - 1]
    },
    restartIntro () {
      document.cookie = 'introjs-dontShowAgain=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/'
      this.startIntro()
      document.cookie = 'introjs-dontShowAgain=true; expires=Thu, 01 Jan 2025 00:00:00 GMT; path=/'
    },
    startIntro () {
      this.intro = introJs().setOptions({
        tooltipClass: 'customTooltipClass',
        buttonClass: 'customButtons',
        nextLabel: '下一个',
        prevLabel: '上一个',
        skipLabel: '跳过',
        doneLabel: '完成',
        hidePrev: true,
        hideNext: false,
        exitOnOverlayClick: false,
        showStepNumbers: false,
        disableInteraction: true,
        showBullets: true,
        showButtons: true,
        dontShowAgain: true,
        dontShowAgainLabel: '不再显示'
      }).start()
    },
    startTutor () {
      document.cookie = 'introjs-dontShowAgain=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/'
      this.startIntro()
    }
  },
  watch: {
    // 使用函数形式的监听器，监听数据属性 "isVisible" 的变化
    color () {
      this.queryPPTTemplates()
    },
    // 另一个属性的监听
    Style () {
      this.queryPPTTemplates()
    },
    watchBoth (newValue, oldValue) {
      if (newValue[0] !== oldValue[0] || newValue[1] !== oldValue[1]) {
        this.$message.warning({ message: '正在编辑第' + (this.selectedChapter) + '章的' + this.selectedSectionString, showClose: true })
        this.jumpSlide(newValue[0], newValue[1])// Call method when both variables change
      }
    },
    selectedPPTTemplateColor (newValue, oldValue) {
      if (newValue !== oldValue) {
        this.$message({
          showClose: true,
          message: '切换颜色请重新选择对应颜色的页面模板',
          type: 'warning'
        })
      }
    }
  },
  mounted () {
    this.updateWidth()
    this.queryPPTTemplates()
    this.selectedPPTTemplateID = this.templates[0].toString()
    this.parseTemplates()
    this.selectedChapter = 1
    this.selectedSection = 1
    setTimeout(() => {
      this.startIntro()
    }, 1000)
  },
  created () {
    this.markdownData = JSON.parse(this.$route.params.markdownData)
    this.templates = JSON.parse(this.$route.params.templates)
    this.id = JSON.parse(this.$route.params.id)
  }
}
</script>

<style>
.image-container {
  margin: 10px;
}

.image-card {
  border: none;
  box-shadow: none;
  transition: box-shadow 0.3s;
  margin-bottom: 5px;
}

.image-card .el-card__body {
  padding: 5%;
}

.image {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.PPTPreview {
  width: 100%;
  /* 宽度动态适应父容器 */
  aspect-ratio: 16 / 9;
  /* 直接指定宽高比为16:9 */
  border: 1px dashed #ccc;
}

/* 控制输入框的样式 */
.chapterTitle .el-input__inner {
  /* 在此处添加你想要的样式 */
  border: 1px dashed #ccc;
  border-width: 2px;
  padding: 5px 10px;
  font-size: large;
  font-weight: bold;
  /* 例如，删除边框、圆角和内边距 */
  border-top: none;
  border-left: none;
  border-right: none;
  border-radius: 0%;
}

.chapterSideTitle .el-input__inner {
  /* 在此处添加你想要的样式 */
  border: 1px dashed #ccc;
  border-width: 2px;
  padding: 5px 10px;
  font-size: medium;
  /* 例如，增加边框、圆角和内边距 */
  border-top: none;
  border-left: none;
  border-right: none;
  border-radius: 0%;
}

.sectionTitle .el-input__inner {
  /* 在此处添加你想要的样式 */
  border: 1px dashed #ccc;
  border-width: 2px;
  padding: 5px 10px;
  font-size: medium;
  /* 例如，增加边框、圆角和内边距 */
  border-top: none;
  border-left: none;
  border-right: none;
  border-radius: 0%;
}

/* 如果需要在不同状态下应用不同的样式，可以使用伪类选择器 */
.sectionTitle .el-input__inner:hover {
  /* 在输入框获得焦点时的样式 */
  outline: none;
  border-color: #155daf;
  /* 例如，去除默认的聚焦样式并改变边框颜色 */
}

/* 如果需要在不同状态下应用不同的样式，可以使用伪类选择器 */
.sectionTitle .el-input__inner:focus {
  /* 在输入框获得焦点时的样式 */
  outline: none;
  border-color: #007bff;
  /* 例如，去除默认的聚焦样式并改变边框颜色 */
}

/* 如果需要在不同状态下应用不同的样式，可以使用伪类选择器 */
.chapterTitle .el-input__inner:hover {
  /* 在输入框获得焦点时的样式 */
  outline: none;
  border-color: #155daf;
  /* 例如，去除默认的聚焦样式并改变边框颜色 */
}

/* 如果需要在不同状态下应用不同的样式，可以使用伪类选择器 */
.chapterTitle .el-input__inner:focus {
  /* 在输入框获得焦点时的样式 */
  outline: none;
  border-color: #007bff;
  /* 例如，去除默认的聚焦样式并改变边框颜色 */
}

/* 如果需要在不同状态下应用不同的样式，可以使用伪类选择器 */
.chapterSideTitle .el-input__inner:hover {
  /* 在输入框获得焦点时的样式 */
  outline: none;
  border-color: #155daf;
  /* 例如，去除默认的聚焦样式并改变边框颜色 */
}

/* 如果需要在不同状态下应用不同的样式，可以使用伪类选择器 */
.chapterSideTitle .el-input__inner:focus {
  /* 在输入框获得焦点时的样式 */
  outline: none;
  border-color: #007bff;
  /* 例如，去除默认的聚焦样式并改变边框颜色 */
}

.sectionTitle {
  margin-left: 2%;
  width: 40%;
}

.sectionBody {
  margin-left: 8%;
  width: 80%;
}

.single-section {
  border-left: 4px solid #5e91c9;
  margin-bottom: 30px;
}

.section-pair {
  border-left: 4px solid #5e91c9;
  margin-bottom: 30px;
}

.text .el-input__inner {
  font-weight: bold;
  border-top: none;
  border-left: none;
  border-right: none;
  border-radius: 0;
}

.el-divider--horizontal {
  margin-top: 0px;
}

.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 47px;
  z-index: 1000;
  /* 确保它在页面的最顶层 */
  background-color: white;
  display: flex;
  align-items: center;
}

.content {
  margin-left: 10%;
  margin-right: 10%;
}

.chapter {
  /* border: 1px solid black; */
  border-left: 6px solid rgb(216, 215, 215);
  border-image: linear-gradient(180deg,
      rgba(255, 255, 255, 0) 0%,
      rgb(216, 215, 215),
      rgba(255, 255, 255, 0) 99%) 2 2 2 2;
  padding-left: 30px;
  margin-bottom: 10px;
  margin-top: 10px;
}

.title {
  font-weight: bold;
}

.customTooltipClass {
  color: black;
  font-size: medium;
  position: relative;
}

.customTooltipClass .introjs-skipbutton {
  font-size: 12px;
  margin: 10px;
}

.introjs-tooltip {
  position: absolute;
  cursor: pointer;
}

.introjs-overlay {
  z-index: 1040 !important
}

.customTooltipClass .no-remind-btn {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 5px 10px;
  font-size: 10px;
  color: #fff;
  background-color: #409EFF;
  border-radius: 4px;
  cursor: pointer;
  z-index: 3000 !important
}

.customTooltipClass .no-remind-btn:hover {
  background-color: #2979FF;
}
</style>
