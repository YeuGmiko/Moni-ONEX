import { request } from '@/service/request';

export interface CreateUser {
  name: string;
  userName: string;
  password: string;
}

export type UpdateUser = CreateUser;

export interface UserInfo {
  userId: string;
  userName: string;
  name: string;
  status: number;
  roles: string[];
}

export interface UpdatePassword {
  oldPassword: string;
  newPassword: string;
}

export function fetchCommonUserList() {
  return request<UserInfo[]>({
    url: '/admin/users/common',
    method: 'GET'
  });
}

export function fetchAdminUserList() {
  return request<UserInfo[]>({
    url: '/admin/users/admin',
    method: 'GET'
  });
}

export function postCommonUser(body: CreateUser) {
  return request({
    url: '/admin/users/common',
    method: 'POST',
    data: body
  });
}

export function postCommonUserBatch(body: CreateUser[]) {
  return request({
    url: '/admin/users/common/batch',
    method: 'POST',
    data: body
  });
}

export function postAdminUser(body: CreateUser) {
  return request({
    url: '/admin/users/admin',
    method: 'POST',
    data: body
  });
}

export function updateUserInfo(id: string, body: UpdateUser) {
  return request({
    url: `/admin/users/${id}`,
    method: 'PUT',
    data: body
  });
}

export function postCommonUserList(list: CreateUser[]) {
  return request({
    url: '/admin/users/common/upload',
    method: 'POST',
    data: list
  });
}

export function deleteCommonUser(id: string) {
  return request({
    url: `/admin/users/common/${id}`,
    method: 'DELETE'
  });
}

export function deleteAdminUser(id: string) {
  return request({
    url: `/admin/users/admin/${id}`,
    method: 'DELETE'
  });
}
export function updateCurrentUserInfo(body: { name: string }) {
  return request({
    url: '/users/info',
    method: 'PUT',
    data: body
  });
}

export function updatePassword(body: UpdatePassword) {
  return request({
    url: '/users/auth/password',
    method: 'PUT',
    data: body
  });
}

export function updateUserBan(id: string, ban: boolean) {
  return request({
    url: `/admin/users/ban/${id}`,
    method: 'PUT',
    params: {
      type: ban ? 1 : 0
    }
  });
}
