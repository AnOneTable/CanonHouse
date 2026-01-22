package com.example.canting.domain.usecase

import com.example.canting.data.repository.IDataControlRepository
import com.example.canting.domain.model.RecorderState
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance

/** 用例：获取记录仪状态流 */
class GetRecorderStateUseCase @Inject constructor(private val repository: IDataControlRepository) {
    operator fun invoke(): Flow<RecorderState> {
        return repository.dataFlow.filterIsInstance<RecorderState>()
    }
}
