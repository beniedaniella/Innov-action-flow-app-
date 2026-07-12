package com.innovaction.finance.di

import com.innovaction.finance.data.export.CsvExportService
import com.innovaction.finance.data.export.ExportService
import com.innovaction.finance.data.export.PdfExportService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExportModule {
    @Binds
    @Singleton
    abstract fun bindExportService(impl: CsvExportService): ExportService
}
