<script setup lang="ts">
import { computed, h, ref, toRefs } from 'vue';
import type { DataTableColumns, UploadFileInfo } from 'naive-ui';
import { NButton, NSpace, NTag, useMessage } from 'naive-ui';
import { readFileAsText } from '@/utils/file';
import type { PostQuestion } from '@/service/api/question';
import { postQuestionBatch } from '@/service/api/question';

interface Props {
  moduleId?: string;
}
interface ParseQuestion {
  title: string;
  content: string;
  answers: string[];
}
type SubmitQuestion = PostQuestion & { text: string };
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'success'): void;
}>();
const showModel = defineModel<boolean>('show', {
  required: true
});
const props = withDefaults(defineProps<Props>(), {
  moduleId: ''
});
const { moduleId } = toRefs(props);
const submiting = ref<boolean>(false);
const message = useMessage();
const currentFile = ref<File | null>();
const tabValue = ref<string>('upload');
const parsedQuestions = ref<ParseQuestion[]>([]);
const repeatQuestions = computed<Set<string>>(() => {
  const map = new Map<string, number>();
  parsedQuestions.value.forEach(({ title }) => {
    let count = map.get(title);
    if (!count) count = 0;
    map.set(title, count + 1);
  });
  const result = new Set<string>();
  map.entries().forEach(([title, count]) => {
    if (count > 1) result.add(title);
  });
  return result;
});
const submits = computed<SubmitQuestion[]>(() => {
  return parsedQuestions.value.map(origin => {
    const { title, content, answers } = origin;

    const options = answers.map((answer, index) => ({ orderNo: index + 1, answer }));
    let submitContent = content;
    options.forEach(option => {
      submitContent = submitContent.replace(
        /【】/,
        `<span class="option-order" data-order="${option.orderNo}">${option.orderNo}</span>`
      );
    });

    return {
      title,
      text: content,
      content: submitContent,
      order: 1,
      options
    };
  });
});

const tableColumns: DataTableColumns<SubmitQuestion> = [
  {
    key: 'no',
    title: '序号',
    render(_, index: number) {
      return `${index + 1}`;
    }
  },
  {
    key: 'title',
    title: '题目'
  },
  {
    key: 'text',
    title: '题目内容'
  },
  {
    key: 'answers',
    title: '选项',
    render(row: SubmitQuestion) {
      const { options } = row;
      return h(NSpace, { vertical: true }, () => options.map(({ answer }) => h(NTag, { type: 'primary' }, answer)));
    }
  },
  {
    key: 'state',
    title: '状态',
    render(row: SubmitQuestion) {
      const { title } = row;
      const isExisted = repeatQuestions.value.has(title);
      return h(
        NTag,
        {
          type: isExisted ? 'error' : 'success'
        },
        // eslint-disable-next-line no-nested-ternary
        isExisted ? '题目已存在' : '正常'
      );
    }
  },
  {
    key: 'op',
    title: '操作',
    render(_, index) {
      return h(NButton, { type: 'error', text: true, onClick: () => handlerRowDelete(index) }, '删除');
    }
  }
];

async function handleUploadFile({ file }: { file: UploadFileInfo; event?: ProgressEvent }) {
  try {
    if (!file.file) return false;
    const mdText = await readFileAsText(file.file);
    parsedQuestions.value = parseMarkdownToQuestions(mdText);
    return true;
  } catch (e) {
    console.error(e);
    return false;
  }
}

async function handleFileDelete() {
  changeTab('upload');
  currentFile.value = null;
}

async function handleUploadChange({
  file
}: {
  file: UploadFileInfo;
  fileList: UploadFileInfo[];
  event?: ProgressEvent;
}) {
  const { status } = file;
  if (status === 'removed') return;
  currentFile.value = file.file;
  changeTab('submit');
}

function parseMarkdownToQuestions(markdownText: string): ParseQuestion[] {
  const questionBlocks = markdownText.split('### ').filter(block => block.trim());
  return questionBlocks.map(block => {
    const lines = block.split('\n').map(line => line.trim());
    const title = lines[0].trim();
    const content = lines[1] ? lines[1].trim() : '';
    const answerLine = lines.find(line => line.startsWith('选项：'));
    const answers = answerLine?.match(/【(.*?)】/g)?.map(match => match.replace(/[【】]/g, '').trim()) || [];
    return { title, content, answers };
  });
}

function handlerRowDelete(index: number) {
  parsedQuestions.value.splice(index, 1);
}

async function submit() {
  submiting.value = true;
  const { error } = await postQuestionBatch(moduleId.value, submits.value);
  if (error) {
    submiting.value = true;
    return;
  }
  message.success('导入成功');
  reset();
  close();
  emit('success');
  emit('close');
}

function changeTab(value: string) {
  if (!currentFile.value) return;
  tabValue.value = value;
}

function reset() {
  parsedQuestions.value = [];
  currentFile.value = null;
  tabValue.value = 'upload';
}

function close() {
  showModel.value = false;
  reset();
  emit('close');
}
</script>

<template>
  <NModal v-model:show="showModel" auto-focus display-directive="if" :mask-closable="false" @close="close">
    <NCard class="max-w-[1250px] w-[80%]" title="批量导入题目" closable @close="close">
      <NTabs animated :value="tabValue">
        <NTabPane name="upload" tab="上传" display-directive="show" @click="changeTab('upload')">
          <template #tab>
            <span @click="changeTab('upload')">上传</span>
          </template>
          <div>
            <a class="mb-1 inline-block text-blue" href="/template/questions-example.md" download>点击下载模板</a>
            <NUpload
              class="mb-2"
              :max="1"
              accept=".txt,.md,text/plain,text/markdown"
              @before-upload="handleUploadFile"
              @change="handleUploadChange"
              @remove="handleFileDelete"
            >
              <NUploadDragger>
                <div class="flex flex-col items-center gap-y-2">
                  <NIcon size="48" :depth="3">
                    <icon-mdi-cloud-upload></icon-mdi-cloud-upload>
                  </NIcon>
                  <NText class="text-3">点击或者拖动文件到该区域来上传</NText>
                </div>
                <NP depth="3">仅支持 md、txt 等文件，文件数据必须是指定的格式</NP>
              </NUploadDragger>
            </NUpload>
          </div>
        </NTabPane>
        <NTabPane name="submit" :disabled="!Boolean(currentFile)">
          <template #tab>
            <span @click="changeTab('submit')">提交</span>
          </template>
          <NDataTable :columns="tableColumns" :data="submits"></NDataTable>
          <div class="mt-2 flex justify-end gap-x-2">
            <NButton @click="close">取消</NButton>
            <NButton type="primary" :disabled="repeatQuestions.size > 0 || submits.length <= 0" @click="submit">
              提交
            </NButton>
          </div>
        </NTabPane>
      </NTabs>
    </NCard>
  </NModal>
</template>

<style scoped></style>
