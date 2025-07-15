package com.undefined.hydron.infrastructure.db.preloaded

import com.undefined.hydron.domain.models.entities.ActividadFisica

val ActividadFisicaDataSet = listOf(
    // General
    ActividadFisica(edadMin = 10, edadMax = 14, grupo = "General", horasPorSemana = 12.9f),
    ActividadFisica(edadMin = 15, edadMax = 19, grupo = "General", horasPorSemana = 12.1f),
    ActividadFisica(edadMin = 20, edadMax = 24, grupo = "General", horasPorSemana = 11.7f),
    ActividadFisica(edadMin = 30, edadMax = 34, grupo = "General", horasPorSemana = 11.4f),
    ActividadFisica(edadMin = 35, edadMax = 39, grupo = "General", horasPorSemana = 11.0f),
    ActividadFisica(edadMin = 45, edadMax = 49, grupo = "General", horasPorSemana = 10.6f),
    ActividadFisica(edadMin = 55, edadMax = 59, grupo = "General", horasPorSemana = 10.0f),
    ActividadFisica(edadMin = 70, edadMax = 74, grupo = "General", horasPorSemana = 9.5f),

    // Diabetes
    ActividadFisica(edadMin = 10, edadMax = 14, grupo = "Diabetes", horasPorSemana = 12.9f),
    ActividadFisica(edadMin = 15, edadMax = 19, grupo = "Diabetes", horasPorSemana = 12.1f),
    ActividadFisica(edadMin = 20, edadMax = 24, grupo = "Diabetes", horasPorSemana = 10.7f),
    ActividadFisica(edadMin = 30, edadMax = 34, grupo = "Diabetes", horasPorSemana = 10.1f),

    // Enfermedad Renal Crónica (ERC)
    ActividadFisica(edadMin = 10, edadMax = 14, grupo = "ERC", horasPorSemana = 11.4f),
    ActividadFisica(edadMin = 15, edadMax = 19, grupo = "ERC", horasPorSemana = 10.3f),
    ActividadFisica(edadMin = 20, edadMax = 24, grupo = "ERC", horasPorSemana = 7.9f),
    ActividadFisica(edadMin = 30, edadMax = 34, grupo = "ERC", horasPorSemana = 7.4f),
    ActividadFisica(edadMin = 35, edadMax = 39, grupo = "ERC", horasPorSemana = 6.9f),

    // Fibrosis Quística (FQ)
    ActividadFisica(edadMin = 10, edadMax = 14, grupo = "FQ", horasPorSemana = 13.6f),
    ActividadFisica(edadMin = 15, edadMax = 19, grupo = "FQ", horasPorSemana = 12.6f),
    ActividadFisica(edadMin = 20, edadMax = 24, grupo = "FQ", horasPorSemana = 11.4f),
    ActividadFisica(edadMin = 30, edadMax = 34, grupo = "FQ", horasPorSemana = 10.9f),
    ActividadFisica(edadMin = 35, edadMax = 39, grupo = "FQ", horasPorSemana = 10.6f),
    ActividadFisica(edadMin = 45, edadMax = 49, grupo = "FQ", horasPorSemana = 10.0f)
)
