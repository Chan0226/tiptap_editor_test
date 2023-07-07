<template>
  <div class='editor'>
    <!-- Menu bar -->
    <div class='editor__menubar' :editor="editor">
      <div class="menubar">
        <button class="menubar__button" @click="setBold">B</button>
        <button class="menubar__button" @click="setItalic">
          <img class="icon" src="@/static/images/icons/italic.svg" style='width:10px;height:10px'/>
        </button>
        <button class="menubar__button" @click="setUnderline">
          <img class="icon" src="@/static/images/icons/underline.svg" style='width:10px;height:10px'/>
        </button>
        <button class="menubar__button" @click="setImage">
          <img class="icon" src="@/static/images/icons/image.svg" style='width:10px;height:10px'/>
        </button>
        <button class="menubar__button" @click="addVideo">
          <img class="icon" src="@/static/images/icons/youtube.svg" style='width:10px;height:10px'/>
        </button>
      </div>
    </div>

    <!-- Editor content -->
    <div class="editor__content" ref="editorContent" :editor="editor" @update="handleUpdate"/>
  </div>
</template>

<script>
import { Editor, Node } from '@tiptap/core'
import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import Image from '@tiptap/extension-image'
import Link from '@tiptap/extension-link'
import { Extension } from '@tiptap/core'

const Video = Node.create({
  name: 'video',

  group: 'block',
  draggable: true,

  addAttributes() {
    return {
      src: {
        default: null,
      },
    }
  },

  parseHTML() {
    return [
      {
        tag: 'iframe',
      },
    ]
  },

  renderHTML({ HTMLAttributes }) {
    return ['iframe', HTMLAttributes]
  },
})

export default {
  data() {
    return {
      editor: null,
    }
  },

  mounted() {
    this.editor = new Editor({
      extensions: [
        StarterKit,
        Underline,
        Image,
        Link,
        Video,
      ],
      content: '',
      onUpdate: ({ editor }) => {
        this.handleUpdate(editor.getHTML())
      },
      element: this.$refs.editorContent,
    })
  },

  beforeDestroy() {
    if (this.editor) {
      this.editor.destroy()
    }
  },

  methods: {
    setBold() {
      this.editor.chain().focus().toggleBold().run()
    },

    setItalic() {
      this.editor.chain().focus().toggleItalic().run()
    },

    setUnderline() {
      this.editor.chain().focus().toggleUnderline().run()
    },
    // 업로드할 이미지를 선택합니다.
    setImage() {
      let input = document.createElement('input');
      input.type = 'file';
      input.accept = 'image/*';
      input.onchange = e => {
        let file = e.target.files[0];
        console.log(file)
        this.uploadImage(file);
      };
      input.click();
    },

    // 이미지 업로드
    uploadImage(file) {
      let formData = new FormData();
      formData.append('uploadfile', file);

      // 이 axios는 동영상 파일을 서버에 업로드하고 URL을 반환해야 합니다.
      this.$axios.post('/upload/file', formData)
        .then(response => {
          let filePath = response.data; // 파일 경로

          this.insertImageToEditor(filePath);
          this.insertToAttach(filePath);
        })
        .catch(error => {
          console.error('이미지 업로드 실패:', error);
        });
    },

    // 로컬에 저장된 이미지를 에디터에 보여주기
    insertImageToEditor(imageSrc) {
      this.editor.chain().focus().setImage({ src: 'http://localhost:8080' + imageSrc }).run() // 이미지가 담겨진 url
    },

    // 이미지 경로 DB 저장
    async insertToAttach(filePath) {
      let data = {
        fileLoc: filePath,
      };
      await this.$axios
        .post("/upload/insertAttach", data)
        .then((res) => {
          console.log("이미지 경로 저장 성공");
        })
        .catch((error) => {
          console.log(error);
        });
    },

    // 동영상 업로드
    addVideo() {
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = 'video/*'
      input.onchange = async () => {
        const file = input.files[0]
        if (file) {
          const url = await this.upload(file) // 이 함수는 동영상 파일을 서버에 업로드하고 URL을 반환해야 합니다.
          console.log("url:"+url)
          this.showVideo("http://localhost:8080"+ url) // 동영상이 담겨진 url
        }
      }
      input.click()
    },

    async upload(file) {
      const formData = new FormData()
      formData.append('uploadfile', file)
      // 동영상 파일을 업로드할 서버 api
      const response = await this.$axios.post('/upload/file', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
        // '/upload' 엔드포인트는 업로드된 파일의 URL을 반환해야 합니다.
        return response.data
    },

    // 업로드된 영상을 iframe 형태로 editor content에 넣어줍니다.
    showVideo(url) {
      this.editor.chain().focus().insertContent(`<iframe src="${url}"></iframe>`).run()
    },

    // 업데이트 내용을 상위컴포넌트(index.vue)에 전달합니다.
    handleUpdate(html) {
      this.$emit('input', html);
    },
  }
}
</script>

<style> 
.editor{
  width:800px;
  height:300px;
  margin:0 auto;
  border:1px solid black; 
}
.menubar{
  height:15%;
  background-color: rgb(241, 241, 239);
  line-height: 45px;
}
.editor__content{
  width:100%;
  height:85%;
}
.menubar__button{
  border: none;
  border-radius: 5px;
  height:25px;
  width:25px;
}
.menubar__button:hover{
  background-color: rgb(225, 226, 226);
  cursor: pointer;
}
.icon{
  width:10px;
  height:10px
}
</style>