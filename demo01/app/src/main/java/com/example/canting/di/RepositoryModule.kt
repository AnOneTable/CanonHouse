package com.example.canting.di

import com.example.canting.data.repository.DataControlRepository
import com.example.canting.data.repository.IDataControlRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * 绑定 DataControlRepository 的实现到其接口。 Hilt 会在需要 IDataControlRepository 时提供 DataControlRepository
     * 的单例实例。
     *
     * @param dataControlRepository DataControlRepository 的具体实现。
     * @return IDataControlRepository 接口的实例。
     */
    @Binds
    @Singleton
    abstract fun bindDataControlRepository(
            dataControlRepository: DataControlRepository
    ): IDataControlRepository
}
