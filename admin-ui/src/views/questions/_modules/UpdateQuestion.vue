<script setup lang="ts">
import 'md-editor-v3/lib/style.css';
import type { ToolbarNames } from 'md-editor-v3';
import { MdEditor, NormalToolbar } from 'md-editor-v3';
import { reactive, ref, toRefs, watch } from 'vue';
import { useMessage } from 'naive-ui';
import type { FormInst, FormRules } from 'naive-ui';
import type { PutQuestion, QuestionOption } from '@/service/api/question';
import { fetchQuestion, fetchQuestionOptions, updateQuestion } from '@/service/api/question';
import { toolbars as totalToolbars } from '../constant';

interface Props {
  questionId?: string;
}
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'success'): void;
}>();
const showModel = defineModel<boolean>('show', {
  required: true
});
const props = withDefaults(defineProps<Props>(), {
  questionId: ''
});
const { questionId } = toRefs(props);
const model = reactive<PutQuestion>({
  title: '',
  order: 0,
  content: ''
});
const loading = ref<boolean>(false);
const submitLoading = ref<boolean>(false);
const options = ref<QuestionOption[]>([]);
const message = useMessage();
const editorRef = ref();
const formRef = ref<FormInst>();
const toolbars: ToolbarNames[] = [0, ...totalToolbars];

const rules: FormRules = {
  title: [
    {
      required: true,
      validator() {
        if (!model.title) return new Error('标题不能为空');
        return true;
      }
    }
  ],
  order: [
    {
      required: true,
      validator() {
        return true;
      }
    }
  ],
  content: [
    {
      required: true,
      validator() {
        if (!model.content) return new Error('题目内容不能为空');
        return true;
      }
    }
  ]
};

function addOption() {
  const orderNo = options.value.length + 1;
  model.content += `<span class="option-order" data-order="${orderNo}">${orderNo}</span>`;
  if (editorRef.value) editorRef.value.focus('end');
}

async function validateQuestionInfo() {
  if (!formRef.value) return false;
  try {
    await formRef.value.validate();
    return true;
  } catch {
    return false;
  }
}
async function submit() {
  const flag = await validateQuestionInfo();
  if (!flag) return;
  if (!questionId.value) {
    message.error(`题目ID非法：${questionId.value || '空值'}`);
    return;
  }
  submitLoading.value = true;
  const { error } = await updateQuestion(questionId.value, model);
  if (error) {
    submitLoading.value = false;
    return;
  }
  submitLoading.value = false;
  message.success('更新成功');
  emit('success');
  close();
}
function close() {
  showModel.value = false;
  reset();
  emit('close');
}
function reset() {
  Object.assign(model, {
    title: '',
    order: 0,
    content: ''
  });
}

watch(showModel, async newValue => {
  if (!newValue || !questionId.value) return;
  loading.value = true;
  const { data: questionInfo, error: e1 } = await fetchQuestion(questionId.value);
  if (e1) {
    loading.value = false;
    return;
  }
  const { data: optionsData, error: e2 } = await fetchQuestionOptions(questionId.value);
  if (e2) {
    loading.value = false;
    return;
  }
  loading.value = false;
  /* update info */
  Object.assign(model, {
    ...questionInfo,
    order: questionInfo.displayOrder
  });
  options.value = optionsData;
});
watch(
  () => model.content,
  newValue => {
    const div = document.createElement('div');
    div.innerHTML = newValue;
    /* reset options */
    Array.from(div.querySelectorAll('span.option-order')).forEach((option, index) => {
      const order = index + 1;
      option.textContent = `${order}`;
      (option as HTMLElement).dataset.order = `${order}`;
    });
    /* set markdown */
    model.content = div.innerHTML;
  },
  {
    immediate: true
  }
);
</script>

<template>
  <NModal v-model:show="showModel" :mask-closable="false" display-directive="if" @close="close">
    <NCard class="max-w-[1400px] w-[80%]" title="编辑题目" closable @close="close">
      <div v-if="loading" class="h-[500px] flex items-center justify-center">
        <icon-line-md-loading-twotone-loop class="text-3xl" />
        <p>加载中</p>
      </div>
      <NForm v-else ref="formRef" v-model="model" :rules="rules" first>
        <div class="flex gap-x-3">
          <NFormItem label="标题" path="title">
            <NInput v-model:value="model.title" placeholder="请输入标题"></NInput>
          </NFormItem>
          <NFormItem label="顺序" path="order">
            <NInputNumber v-model:value="model.order" placeholder="请输入顺序，小数靠前" :min="0"></NInputNumber>
          </NFormItem>
        </div>
        <div class="flex gap-x-2">
          <NFormItem class="flex-grow" label="题目内容" path="content">
            <div class="h-[500px] w-full flex gap-x-2">
              <MdEditor ref="editorRef" v-model="model.content" :toolbars="toolbars">
                <template #defToolbars>
                  <NormalToolbar title="Add Question"></NormalToolbar>
                </template>
              </MdEditor>
            </div>
          </NFormItem>
          <NFormItem v-show="options.length > 0" class="flex-grow" label="答案" path="options">
            <NScrollbar trigger="none" class="h-[500px] py-1 pr-3">
              <NForm label-placement="left" :show-require-mark="false" label-align="right">
                <NFormItem
                  v-for="option in options"
                  :key="option.id"
                  :label-style="{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }"
                >
                  <template #label>
                    <span class="option-order">{{ option.orderNo }}</span>
                  </template>
                  <NInput v-model:value="option.answer" disabled placeholder="请输入答案"></NInput>
                </NFormItem>
              </NForm>
            </NScrollbar>
          </NFormItem>
        </div>
      </NForm>
      <p class="mt-3 flex justify-end gap-x-2">
        <NButton @click="close">取消</NButton>
        <NButton type="primary" ghost :loading="submitLoading" @click="submit">提交</NButton>
      </p>
    </NCard>
  </NModal>
</template>

<style>
/* markdown editor area style */
.cm-scroller {
  background-color: rgba(0, 0, 0, 0.05);
}
.option-order {
  margin: 0 5px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  line-height: 1;
  border: 2px solid #cc7cee;
  background-color: white;
  color: #cc7cee;
  border-radius: 26px;
}
</style>
