<script setup lang="ts">
import { computed, h, ref } from 'vue';
import type { DataTableColumns, UploadFileInfo } from 'naive-ui';
import { NButton, NTag, useMessage } from 'naive-ui';
import { readFileAsText } from '@/utils/file';
import type { CreateUser } from '@/service/api/user';
import { postCommonUserBatch } from '@/service/api/user';

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'success'): void;
}>();
const showModel = defineModel<boolean>('show', {
  required: true
});
const submiting = ref<boolean>(false);
const message = useMessage();
const currentFile = ref<File | null>();
const tabValue = ref<string>('upload');
const parsedUsers = ref<CreateUser[]>([]);
const repeatUsers = computed<Set<string>>(() => {
  const map = new Map<string, number>();
  parsedUsers.value.forEach(({ userName }) => {
    let count = map.get(userName);
    if (!count) count = 0;
    map.set(userName, count + 1);
  });
  const result = new Set<string>();
  map.entries().forEach(([userName, count]) => {
    if (count > 1) result.add(userName);
  });
  return result;
});

const tableColumns: DataTableColumns<CreateUser> = [
  {
    key: 'no',
    title: '序号',
    render(_, index: number) {
      return `${index + 1}`;
    }
  },
  {
    key: 'name',
    title: '昵称'
  },
  {
    key: 'userName',
    title: '注册用户名'
  },
  {
    key: 'password',
    title: '初始密码'
  },
  {
    key: 'state',
    title: '状态',
    render(row: CreateUser) {
      const { userName } = row;
      const isExisted = repeatUsers.value.has(userName);
      return h(
        NTag,
        {
          type: isExisted ? 'error' : 'success'
        },
        // eslint-disable-next-line no-nested-ternary
        isExisted ? '用户名已存在' : '正常'
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
    parsedUsers.value = parseMarkDownToUsers(mdText);
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

function parseMarkDownToUsers(markdownText: string): CreateUser[] {
  const userBlocks = markdownText.split('### ').filter(block => block.trim());
  return userBlocks.map(block => {
    const lines = block.split('\n').filter(line => line.trim());
    const name = lines[0].trim();
    const userName =
      lines
        .find(line => line.startsWith('用户名：'))
        ?.trim()
        ?.replace(/用户名：/g, '') ?? '';
    const password =
      lines
        .find(line => line.startsWith('密码：'))
        ?.trim()
        .replace(/密码：/g, '') ?? '';
    return { name, userName, password };
  });
}

function handlerRowDelete(index: number) {
  parsedUsers.value.splice(index, 1);
}

async function submit() {
  submiting.value = true;
  const { error } = await postCommonUserBatch(parsedUsers.value);
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
  parsedUsers.value = [];
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
    <NCard class="max-w-[950px] w-[80%]" title="批量导入题目" closable @close="close">
      <NTabs animated :value="tabValue">
        <NTabPane name="upload" tab="上传" display-directive="show" @click="changeTab('upload')">
          <template #tab>
            <span @click="changeTab('upload')">上传</span>
          </template>
          <div>
            <a class="mb-1 inline-block text-blue" href="/template/users-example.md" download>点击下载模板</a>
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
          <NDataTable :columns="tableColumns" :data="parsedUsers"></NDataTable>
          <div class="mt-2 flex justify-end gap-x-2">
            <NButton @click="close">取消</NButton>
            <NButton type="primary" :disabled="repeatUsers.size > 0 || parsedUsers.length <= 0" @click="submit">
              提交
            </NButton>
          </div>
        </NTabPane>
      </NTabs>
    </NCard>
  </NModal>
</template>

<style scoped></style>
