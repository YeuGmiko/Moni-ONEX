import type { TableColumns } from 'naive-ui/es/data-table/src/interface';
import { h } from 'vue';
import { NButton } from 'naive-ui';
import type { UserInfo } from '@/service/api/user';

export const useTableColumns: TableColumns<UserInfo> = [
  {
    type: 'selection'
  },
  {
    key: 'order',
    title: 'No',
    render(_: UserInfo, index: number) {
      return h('div', {}, `${index + 1}`);
    }
  },
  {
    key: 'name',
    title: '用户昵称'
  },
  {
    key: 'userName',
    title: '用户名'
  },
  {
    key: 'op',
    title: '操作',
    render(row: UserInfo) {
      return h(
        NButton,
        {
          bordered: true,
          onClick: () => {
            console.log(row);
          }
        },
        '删除'
      );
    }
  }
];
