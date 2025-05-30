<script setup lang="ts">
import 'md-editor-v3/lib/style.css';
import type { ToolbarNames } from 'md-editor-v3';
import { MdEditor, NormalToolbar } from 'md-editor-v3';
import { reactive, ref, toRefs, watch } from 'vue';
import { useMessage } from 'naive-ui';
import type { FormInst, FormRules } from 'naive-ui';
import type { PostQuestion, PostQuestionOption } from '@/service/api/question';
import { postQuestion } from '@/service/api/question';
import { toolbars as totalToolbars } from '../constant';

interface Props {
  defaultOrder?: number;
  moduleId?: string;
}
const props = withDefaults(defineProps<Props>(), {
  defaultOrder: 0,
  moduleId: ''
});
const { defaultOrder, moduleId } = toRefs(props);
const model = reactive<PostQuestion>({
  title: '',
  order: 0,
  content: '',
  options: []
});
const showModel = defineModel<boolean>('show', {
  required: true
});
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'success'): void;
}>();
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
  ],
  options: [
    {
      required: true,
      validator() {
        return model.options.every(option => option.answer !== '') ? true : new Error('请填写完所有答案');
      }
    }
  ]
};
watch(
  () => model.content,
  newValue => {
    const div = document.createElement('div');
    div.innerHTML = newValue;
    /* reset options */
    const options = Array.from(div.querySelectorAll('span.option-order'));
    options.forEach((option, index) => {
      const order = index + 1;
      option.textContent = `${order}`;
      (option as HTMLElement).dataset.order = `${order}`;
    });
    const orders: string[] = options.map((dom, index) => {
      return (dom as HTMLElement).dataset.order || `${index + 1}`;
    });
    /* set markdown */
    model.content = div.innerHTML;
    /* set options */
    model.options = orders.map(order => {
      const option = model.options.find(q => `${q.orderNo}` === order);
      if (option) return option;
      return {
        orderNo: Number.parseInt(order, 10),
        answer: ''
      };
    });
  },
  {
    immediate: true
  }
);

function addOption() {
  model.options.forEach((option, index) => (option.orderNo = index + 1));
  const orderNo = model.options.length + 1;
  const option: PostQuestionOption = {
    orderNo,
    answer: ''
  };
  model.options.push(option);
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
async function submit(continued?: boolean) {
  const flag = await validateQuestionInfo();
  if (!flag) return;
  if (!moduleId.value) {
    message.error(`模块ID非法：${moduleId.value || '空值'}`);
    return;
  }
  const { error } = await postQuestion(moduleId.value, model);
  if (error) return;
  message.success('新增成功');
  emit('success');
  if (!continued) {
    close();
  } else {
    reset();
  }
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
    content: '',
    options: []
  });
}
watch(defaultOrder, newValue => {
  model.order = newValue;
});
</script>

<template>
  <NModal v-model:show="showModel" :mask-closable="false" display-directive="if" @close="close">
    <NCard class="max-w-[1400px] w-[80%]" title="添加题目" closable @close="close">
      <NForm ref="formRef" v-model="model" :rules="rules" first>
        <div class="flex gap-x-3">
          <NFormItem label="标题" path="title">
            <NInput v-model:value="model.title" placeholder="请输入标题"></NInput>
          </NFormItem>
          <NFormItem label="顺序" path="order">
            <NInputNumber
              v-model:value="model.order"
              placeholder="请输入顺序，小数靠前"
              :default-value="defaultOrder"
              :min="0"
            ></NInputNumber>
          </NFormItem>
        </div>
        <div class="flex gap-x-2">
          <NFormItem class="flex-grow" label="题目内容" path="content">
            <div class="h-[500px] w-full flex gap-x-2">
              <MdEditor ref="editorRef" v-model="model.content" :toolbars="toolbars">
                <template #defToolbars>
                  <NormalToolbar title="Add Question">
                    <template #trigger>
                      <NButton type="primary" ghost size="small" @click="addOption">
                        <template #icon>
                          <icon-material-symbols-light-add-circle-outline />
                        </template>
                        添加填空
                      </NButton>
                    </template>
                  </NormalToolbar>
                </template>
              </MdEditor>
            </div>
          </NFormItem>
          <NFormItem v-show="model.options.length > 0" class="flex-grow" label="答案" path="options">
            <NScrollbar trigger="none" class="h-[500px] py-1 pr-3">
              <NForm label-placement="left" :show-require-mark="false" label-align="right">
                <NFormItem
                  v-for="option in model.options"
                  :key="option.orderNo"
                  :label-style="{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }"
                >
                  <template #label>
                    <span class="option-order">{{ option.orderNo }}</span>
                  </template>
                  <NInput v-model:value="option.answer" placeholder="请输入答案"></NInput>
                </NFormItem>
              </NForm>
            </NScrollbar>
          </NFormItem>
        </div>
      </NForm>
      <p class="mt-3 flex justify-end gap-x-2">
        <NButton @click="close">取消</NButton>
        <NButton type="primary" ghost @click="submit(false)">提交</NButton>
        <NButton type="primary" @click="submit(true)">继续添加</NButton>
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
