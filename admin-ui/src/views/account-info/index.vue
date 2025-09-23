<script setup lang="ts">
import { ref } from 'vue';
import { NButton, NInput, NModal, useMessage } from 'naive-ui';
import { fetchGetUserInfo } from '@/service/api/auth';
import { updateUserInfo, updatePassword } from '@/service/api/user';

const message = useMessage();
const userInfo = ref({
  userId: '',
  name: '',
  userName: '',
  roles: [] as string[]
});
const showPasswordModal = ref(false);
const passwordForm = ref({
  oldPassword: '',
  newPassword: ''
});

// 获取用户信息
async function getUserInfo() {
  const { data } = await fetchGetUserInfo();
  userInfo.value = data;
}

// 保存用户信息
async function handleSave() {
  const { error } = await updateUserInfo({
    name: userInfo.value.name
  });
  if (error) return;
  message.success('保存成功');
  await getUserInfo();
}

// 修改密码
async function handleUpdatePassword() {
  const { error } = await updatePassword(passwordForm.value);
  if (error) return;
  message.success('密码修改成功');
  showPasswordModal.value = false;
  passwordForm.value = { oldPassword: '', newPassword: '' };
}

// 初始化获取用户信息
getUserInfo();
</script>

<template>
  <div class="flex flex-col gap-y-3">
    <NCard title="账号信息" class="flex-grow">
      <div class="flex flex-col gap-4 p-4">
        <div class="flex items-center">
          <span class="w-24 text-left">用户昵称：</span>
          <NInput v-model:value="userInfo.name" class="w-64" />
        </div>
        <div class="flex items-center">
          <span class="w-24 text-left">注册用户名：</span>
          <span>{{ userInfo.userName }}</span>
        </div>
        <div class="flex items-center">
          <span class="w-24 text-left ">密码：</span>
          <span>已设置</span>
          <NButton text type="primary" class="ml-2 underline" @click="showPasswordModal = true">
            修改密码
          </NButton>
        </div>
        <div class="flex ">
          <NButton type="primary" @click="handleSave">保存</NButton>
        </div>
      </div>
    </NCard>

    <NModal v-model:show="showPasswordModal" title="修改密码">
      <NCard style="width: 500px" title="修改密码" :bordered="false" size="large">
        <div class="flex flex-col gap-4">
          <div class="flex items-center">
            <span class="w-24">旧密码：</span>
            <NInput v-model:value="passwordForm.oldPassword" type="password" show-password-on="click" />
          </div>
          <div class="flex items-center">
            <span class="w-24">新密码：</span>
            <NInput v-model:value="passwordForm.newPassword" type="password" show-password-on="click" />
          </div>
          <div class="flex justify-end gap-2">
            <NButton @click="showPasswordModal = false">取消</NButton>
            <NButton type="primary" @click="handleUpdatePassword">确定</NButton>
          </div>
        </div>
      </NCard>
    </NModal>
  </div>
</template>
