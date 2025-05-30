import { request } from '@/service/request';
import type { Question } from '@/service/api/question';

export interface TaskRank {
  rank: number;
  name: string;
  userName: string;
  rightOptions: number;
  submitOptions: number;
  totalOptions: number;
  rightQuestions: number;
  submitQuestion: number;
  totalQuestions: number;
}

export interface TaskConfig {
  genCount: number;
  modules: string[];
}

export function fetchTaskDailyQuestions() {
  return request<Question[]>({
    url: '/task/questions',
    method: 'GET'
  });
}

export function fetchTaskDailyRank() {
  return request<TaskRank[]>({
    url: '/task/rank/daily',
    method: 'GET'
  });
}
export function fetchTaskConfig() {
  return request<TaskConfig>({
    url: '/task/config',
    method: 'GET'
  });
}
export function updateTaskConfig(body: TaskConfig) {
  return request({
    url: '/task/config',
    method: 'PUT',
    data: body
  });
}

export function putTaskQuestions() {
  return request({
    url: '/task/questions',
    method: 'PUT'
  });
}
