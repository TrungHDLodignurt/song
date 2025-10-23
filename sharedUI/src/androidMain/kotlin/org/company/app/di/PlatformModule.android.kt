package org.company.app.di
import android.content.Context
import org.koin.core.module.Module
import org.company.app.domain.player.AndroidPlayerService
import org.company.app.domain.player.IPlayerService
import org.koin.dsl.module
actual val platformModule: Module = module {
    // Công thức 1: Cách tạo IPlayerService
    factory<IPlayerService> {
        // chúng ta chỉ cần yêu cầu Koin "lấy" (get) Context.
        //  Koin sẽ tự tìm Context mà chúng ta cung cấp ở Giai đoạn 4.
        AndroidPlayerService(get<Context>())
    }

}