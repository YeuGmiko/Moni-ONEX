import { request } from '@/service/request';

export interface CreateUser {
  name: string;
  userName: string;
  password: string;
}

export interface UserInfo {
  userId: string;
  userName: string;
  name: string;
  roles: string[];
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
