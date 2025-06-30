package com.undefined.hydron.domain.useCases.wear

import com.undefined.hydron.domain.interfaces.dao.IWearMessageDao

class HandleWearMessage(
    private val dao: IWearMessageDao
) {
    suspend operator fun invoke(path: String, data: Int) {
        dao.handleMessage(path, data)
    }
}
