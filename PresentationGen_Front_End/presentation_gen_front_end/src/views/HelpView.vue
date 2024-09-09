<template>
    <div class="container">
        <div class="frame" id="first">
            <img :src="require('@/assets/logo1.gif')" />
            <div class="text">
                <div class="texttitle">
                    <p>About Presentation Gen</p>
                </div>
                <div class="textmain" style="width: 500px;">
                    <span>
                    PresentationGen是一个用于帮助用户生成演示文稿的网页工具。<br>
                    用户输入一个关键词，PresentationGen即可调用大语言模型生成演示大纲,并根据预定义的模板生成演示文稿，帮助用户轻松准备演讲。<br>
                    PreGen具有直观友好的界面，让用户能够轻松快速地使用各项功能，
                    选择不同的演示文稿以满足不同的需求和场景。
                </span>
                </div>

            </div>
        </div>
        <div class="frame">
            <div class="text">
                <div class="texttitle">
                    <p>How to Contact Us</p>
                </div>
                <div class="textmain" id="third">
                    <p>QQ：?????????????????????????</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {

}
</script>

<style scoped>
.container {
    padding-bottom: 50px;
}
.frame {
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
    height: 500px;
    width:1200px;
    margin: 100px;
    margin-left:200px;
    display: flex;
    flex-direction: row;
}
#first {
    align-items: center;
    justify-content: center;
}
img {
    max-height: 300px;
    max-width: 100%;
}
.text {
    margin-left: 50px;
    font-size: 19px;
}
.texttitle {
    font-family: Arial black ;
    font-size: 35px;
}
.textmain {
    color:rgb(71, 70, 70);
    font-family: 'Times New Roman', Times, serif;
    font-size: 22px;
}
#third {
    display: flex;
    flex-direction: column;
    justify-content: center;
    line-height: 50px;
}
</style>
