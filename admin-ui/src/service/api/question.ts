import { request } from '@/service/request';

export interface Question {
  id: string;
  displayOrder: number;
  level: number;
  title: string;
  content: string;
  moduleId: string;
  createTime: string;
  optionsLen: number;
  moduleName: string;
}

export interface QuestionOption {
  id: string;
  orderNo: number;
  answer: string;
}


export function fetchQuestions(moduleId: string) {
  return request<Question[]>({
    url: `/admin/modules/${moduleId}/questions`,
    method: 'GET'
  });
}

export function fetchQuestion(id: string) {
  return request<Question>({
    url: `/admin/modules/questions/${id}`,
    method: 'GET'
  });
}
export interface PostQuestionOption {
  orderNo: number;
  answer: string;
}
export interface PostQuestion {
  title: string;
  content: string;
  order: number;
  options: PostQuestionOption[];
}
export interface PutQuestion {
  title: string;
  content: string;
  order: number;
}
export function postQuestion(moduleId: string, body: PostQuestion) {
  return request({
    url: `/admin/modules/${moduleId}/questions`,
    method: 'POST',
    data: body
  });
}

export function postQuestionBatch(moduleId: string, body: PostQuestion[]) {
  return request({
    url: `/admin/modules/${moduleId}/questions/batch`,
    method: 'POST',
    data: body
  });
}

export function updateQuestion(id: string, body: PutQuestion) {
  return request({
    url: `/admin/modules/questions/${id}`,
    method: 'PUT',
    data: body
  });
}

export function deleteQuestion(id: string) {
  return request({
    url: `/admin/modules/questions/${id}`,
    method: 'DELETE'
  });
}

export function fetchQuestionOptions(questionId: string) {
  return request<QuestionOption[]>({
    url: `/admin/modules/questions/${questionId}/options`,
    method: 'GET'
  });
}
