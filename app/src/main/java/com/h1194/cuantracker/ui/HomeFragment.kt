package com.h1194.cuantracker.ui;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.lifecycleScope;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.h1194.cuantracker.R;
import com.h1194.cuantracker.data.AppDatabase;
import com.h1194.cuantracker.databinding.FragmentHomeBinding;
import com.h1194.cuantracker.ui.adapter.HomeAdapter
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            setupLineChart(binding.lineChart)
            setupRecyclerView()
            setupProfitCard()
        }

        return view
    }

    private suspend fun setupLineChart(lineChart: LineChart) {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM", Locale("id"))
        val months = mutableListOf<String>()
        val pendapatanEntries = mutableListOf<Entry>()
        val pengeluaranEntries = mutableListOf<Entry>()

        for (i in 5 downTo 0) {
            calendar.add(Calendar.MONTH, -i)
            val monthStr = monthFormat.format(calendar.time)
            months.add(monthStr)

            val monthStart = calendar.clone() as Calendar
            monthStart.set(Calendar.DAY_OF_MONTH, 1)
            val monthEnd = calendar.clone() as Calendar
            monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))

            val pendapatan = db.transactionDao().getTotalByTypeAndDateRange(
                    "Pendapatan",
                    monthStart.time,
                    monthEnd.time
            )
            val pengeluaran = db.transactionDao().getTotalByTypeAndDateRange(
                    "Pengeluaran",
                    monthStart.time,
                    monthEnd.time
            )

            pendapatanEntries.add(Entry(5-i.toFloat(), pendapatan))
            pengeluaranEntries.add(Entry(5-i.toFloat(), pengeluaran))

            calendar.add(Calendar.MONTH, i)
        }

        val pendapatanDataSet = LineDataSet(pendapatanEntries, "Pendapatan").apply {
            color = resources.getColor(R.color.green)
            setCircleColor(resources.getColor(R.color.green))
            valueTextColor = resources.getColor(R.color.white)
            valueTextSize = 12f
            lineWidth = 2f
        }

        val pengeluaranDataSet = LineDataSet(pengeluaranEntries, "Pengeluaran").apply {
            color = resources.getColor(R.color.red)
            setCircleColor(resources.getColor(R.color.red))
            valueTextColor = resources.getColor(R.color.white)
            valueTextSize = 12f
            lineWidth = 2f
        }

        lineChart.apply {
            data = LineData(pendapatanDataSet, pengeluaranDataSet)
            description.isEnabled = false
            legend.textColor = resources.getColor(R.color.white)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(months)
                textColor = resources.getColor(R.color.white)
                gridColor = resources.getColor(R.color.white)
                granularity = 1f
            }

            axisLeft.apply {
                textColor = resources.getColor(R.color.white)
                gridColor = resources.getColor(R.color.white)
            }

            axisRight.isEnabled = false

            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)

            animateX(1000)
        }
    }

    private suspend fun setupProfitCard() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val monthEnd = calendar.time

        val totalPendapatan = db.transactionDao().getTotalByTypeAndDateRange("Pendapatan", monthStart, monthEnd)
        val totalPengeluaran = db.transactionDao().getTotalByTypeAndDateRange("Pengeluaran", monthStart, monthEnd)
        val profit = totalPendapatan - totalPengeluaran

        val formattedProfit = currencyFormatter.format(profit.toDouble()).replace("IDR", "Rp")
        binding.textViewProfit.text = "Keuntungan Bulan Ini: $formattedProfit"

        binding.buttonPrint.setOnClickListener {
            exportToExcel(totalPendapatan, totalPengeluaran, profit)
        }
    }

    private fun exportToExcel(pendapatan: Float, pengeluaran: Float, profit: Float) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Laporan Keuangan")
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Pendapatan")
        headerRow.createCell(1).setCellValue("Pengeluaran")
        headerRow.createCell(2).setCellValue("Keuntungan")

        val dataRow = sheet.createRow(1)
        dataRow.createCell(0).setCellValue(pendapatan.toDouble())
        dataRow.createCell(1).setCellValue(pengeluaran.toDouble())
        dataRow.createCell(2).setCellValue(profit.toDouble())

        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Laporan_Keuangan.xlsx")
        FileOutputStream(file).use {
            workbook.write(it)
        }

        workbook.close()

        Toast.makeText(requireContext(), "Laporan disimpan di: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }

    private suspend fun setupRecyclerView() {
        val transactions = db.transactionDao().getAllTransactions()
        val adapter = HomeAdapter(transactions)
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewHistory.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
