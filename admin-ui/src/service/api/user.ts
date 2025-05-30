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
    url: '/users/common',
    method: 'GET'
  });
}

export function fetchAdminUserList() {
  return request<UserInfo[]>({
    url: '/users/admin',
    method: 'GET'
  });
}

export function postCommonUser(body: CreateUser) {
  return request({
    url: '/users/common',
    method: 'POST',
    data: body
  });
}

export function postCommonUserBatch(body: CreateUser[]) {
  return request({
    url: '/users/common/batch',
    method: 'POST',
    data: body
  });
}

export function postAdminUser(body: CreateUser) {
  return request({
    url: '/users/admin',
    method: 'POST',
    data: body
  });
}

export function postCommonUserList(list: CreateUser[]) {
  return request({
    url: '/users/common/upload',
    method: 'POST',
    data: list
  });
}

export function deleteCommonUser(id: string) {
  return request({
    url: `/users/common/${id}`,
    method: 'DELETE'
  });
}

export function deleteAdminUser(id: string) {
  return request({
    url: `/users/admin/${id}`,
    method: 'DELETE'
  });
}
