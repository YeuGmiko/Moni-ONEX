import { request } from '@/service/request';

export interface Module {
  id: string;
  displayOrder: number;
  label: string;
  name: string;
  bgColor: string;
  remark: string;
}

export function fetchModules() {
  return request<Module[]>({
    url: '/admin/modules',
    method: 'GET'
  });
}

export function fetchModule(id: string) {
  return request<Module>({
    url: `/admin/modules/${id}`,
    method: 'GET'
  });
}

interface PutModule {
  displayOrder: number;
  label: string;
  name: string;
  bgColor: string;
  remark: string;
}
export function updateModule(id: string, body: PutModule) {
  return request({
    url: `/admin/modules/${id}`,
    method: 'PUT',
    data: body
  });
}

export interface PostModule {
  label: string;
  name: string;
  bgColor: string;
  order: number;
  remark: string;
}
export function postModule(body: PostModule) {
  return request({
    url: '/admin/modules',
    method: 'POST',
    data: body
  });
}

export function deleteModule(id: string) {
  return request({
    url: `/admin/modules/${id}`,
    method: 'DELETE'
  });
}
