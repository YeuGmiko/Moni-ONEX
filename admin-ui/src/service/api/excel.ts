import type { CreateUser } from '@/service/api/user';
import { request } from '../request';

export function parseUserExcel(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return request<CreateUser[]>({
    url: '/excel/common',
    method: 'POST',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: formData
  });
}
