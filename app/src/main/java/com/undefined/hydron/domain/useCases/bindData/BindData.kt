package com.undefined.hydron.domain.useCases.bindData

import com.undefined.hydron.domain.models.TrackingPayload
import com.undefined.hydron.domain.repository.interfaces.IBindDataRepository

class BindData (private  val repository: IBindDataRepository) {
    suspend operator fun invoke(payload: TrackingPayload) = repository.bindData(payload)
}