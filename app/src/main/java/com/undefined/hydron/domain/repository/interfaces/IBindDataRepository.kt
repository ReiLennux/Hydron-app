package com.undefined.hydron.domain.repository.interfaces

import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.domain.models.TrackingPayload

interface IBindDataRepository {
    suspend fun bindData(payload: TrackingPayload): Response<Boolean>
}