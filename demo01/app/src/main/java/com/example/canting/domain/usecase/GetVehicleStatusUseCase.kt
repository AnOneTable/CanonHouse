package com.example.canting.domain.usecase

import com.example.canting.data.repository.IDataControlRepository
import com.example.canting.domain.model.VehicleStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance

/** 用例：获取车辆状态流 职责：从总线数据流中过滤出 VehicleStatus 类型的消息 */
class GetVehicleStatusUseCase @Inject constructor(private val repository: IDataControlRepository) {
    operator fun invoke(): Flow<VehicleStatus> {
        return repository.dataFlow.filterIsInstance<VehicleStatus>()
    }
}
