import request from './request'

export interface Task {
  id: string
  userId: string
  promptText: string
  modelName: string
  status: 'pending' | 'processing' | 'completed' | 'failed'
  tokenCount: number
  latency: number
  resultText: string
  errorMsg: string
  createTime: string
}

export interface ModelStat {
  modelName: string
  taskCount: number
  avgLatencyMs: number
  totalTokens: number
  estimatedCostUSD: number
}

export interface Stats {
  totalTasks: number
  totalTokens: number
  modelStats: ModelStat[]
}

export const login = (userId: number) =>
  request.get(`/user/login?userId=${userId}`)

export const submitEval = (promptText: string, modelName: string) =>
  request.post('/eval/submit', { promptText, modelName })

export const fetchTasks = () =>
  request.get<any, Task[]>('/eval/list')

export const fetchTaskDetail = (taskId: string) =>
  request.get<any, Task>(`/eval/task/${taskId}`)

export const fetchStats = () =>
  request.get<any, Stats>('/eval/stats')

export const submitBatch = (promptTexts: string[], modelNames: string[]) =>
  request.post('/eval/batch', { promptTexts, modelNames })
