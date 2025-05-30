<script setup lang="ts">
import 'md-editor-v3/lib/style.css';
import { MdPreview } from 'md-editor-v3';
import { reactive, ref, toRefs, watch } from 'vue';
import type { FormInst } from 'naive-ui';
import type { PutQuestion, QuestionOption } from '@/service/api/question';
import { fetchQuestion, fetchQuestionOptions } from '@/service/api/question';

interface Props {
  questionId?: string;
}
const emit = defineEmits<{
  (e: 'close'): void;
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
const options = ref<QuestionOption[]>([]);
const formRef = ref<FormInst>();

function close() {
  showModel.value = false;
  emit('close');
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
</script>

<template>
  <NModal v-model:show="showModel" display-directive="if" @close="close">
    <NCard class="max-w-[900px] w-[80%]" title="添加题目" closable @close="close">
      <div v-if="loading" class="h-[500px] flex items-center justify-center">
        <icon-line-md-loading-twotone-loop class="text-3xl" />
        <p>加载中</p>
      </div>
      <NForm v-else ref="formRef" v-model="model" first>
        <div class="flex gap-x-3">
          <NFormItem label="标题" path="title">
            <NInput v-model:value="model.title" placeholder="请输入标题" disabled></NInput>
          </NFormItem>
        </div>
        <div class="flex gap-x-2">
          <NFormItem class="max-w-[500px]" label="题目内容" path="content">
            <div class="w-full flex self-start gap-x-2">
              <MdPreview v-model="model.content"></MdPreview>
            </div>
          </NFormItem>
          <NFormItem v-show="options.length > 0" class="min-w-[200px] flex-grow" label="答案" path="options">
            <NScrollbar trigger="none" class="max-h-[400px] py-1 pr-3">
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
    </NCard>
  </NModal>
</template>

<style>
/* markdown editor area style */
.md-editor-previewOnly {
  padding: 5px;
  background-color: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
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
