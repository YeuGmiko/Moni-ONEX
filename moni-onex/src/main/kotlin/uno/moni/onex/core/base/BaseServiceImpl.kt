package uno.moni.onex.core.base

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service

@Service
abstract class BaseServiceImpl<M : BaseMapper<T>, T> : ServiceImpl<M, T>(), BaseService<T> {
    fun getMapper(): M {
        return this.baseMapper
    }
}