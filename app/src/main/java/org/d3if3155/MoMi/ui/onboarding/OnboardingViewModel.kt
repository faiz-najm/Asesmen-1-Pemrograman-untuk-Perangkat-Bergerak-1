import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.d3if3155.MoMi.R
import org.d3if3155.MoMi.model.Starter

class OnboardingViewModel : ViewModel() {
    private val data = MutableLiveData<List<Starter>>()

    init {
        data.value = setupList()

    }

    private fun setupList(): List<Starter> {
        // title, desk ,image
        return listOf(
            Starter(
                "Selamat Datang di MoMi",
                "MoMi adalah aplikasi yang dapat membantu anda menghitung uang",
                R.drawable.ic_launcher_foreground
            ),
            Starter(
                "Atur Anggaran",
                "Tambah anggaran untuk mengontrol pengeluaran",
                R.drawable.ic_launcher_foreground
            ),
        )
    }

    fun getData(): MutableLiveData<List<Starter>> {
        return data
    }
}

