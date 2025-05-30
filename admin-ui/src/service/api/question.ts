import { request } from '@/service/request';

export interface Module {
  id: string;
  displayOrder: number;
  label: string;
  name: string;
  bgColor: string;
  remark: string;
}

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

export function fetchModules() {
  return request<Module[]>({
    url: '/modules',
    method: 'GET'
  });
}

export function fetchModule(id: string) {
  return request<Module>({
    url: `/modules/${id}`,
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
    url: `/modules/${id}`,
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
    url: '/modules',
    method: 'POST',
    data: body
  });
}

export function deleteModule(id: string) {
  return request({
    url: `/modules/${id}`,
    method: 'DELETE'
  });
}

export function fetchQuestions(moduleId: string) {
  return request<Question[]>({
    url: `/modules/${moduleId}/questions`,
    method: 'GET'
  });
}

export function fetchQuestion(id: string) {
  return request<Question>({
    url: `/modules/questions/${id}`,
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
    url: `/modules/${moduleId}/questions`,
    method: 'POST',
    data: body
  });
}

export function postQuestionBatch(moduleId: string, body: PostQuestion[]) {
  return request({
    url: `/modules/${moduleId}/questions/batch`,
    method: 'POST',
    data: body
  });
}

export function updateQuestion(id: string, body: PutQuestion) {
  return request({
    url: `/modules/questions/${id}`,
    method: 'PUT',
    data: body
  });
}

export function deleteQuestion(id: string) {
  return request({
    url: `/modules/questions/${id}`,
    method: 'DELETE'
  });
}

export function fetchQuestionOptions(questionId: string) {
  return request<QuestionOption[]>({
    url: `/modules/questions/${questionId}/options`,
    method: 'GET'
  });
}
