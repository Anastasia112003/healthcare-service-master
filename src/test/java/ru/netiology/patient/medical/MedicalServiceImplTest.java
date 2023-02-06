package ru.netiology.patient.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceImplTest {
    @Test
    void checkBloodPressure() {
        PatientInfoFileRepository fileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(fileRepository.getById(Mockito.anyString()))
                .thenReturn(new PatientInfo("125", "Александр", "Сергеевич",
                        LocalDate.of(2012, 3, 11),
                        new HealthInfo(new BigDecimal(23), new BloodPressure(145, 84))));
        SendAlertService alertServiceMock = Mockito.mock(SendAlertServiceImpl.class);

        MedicalService medicalService = new MedicalServiceImpl(fileRepository, alertServiceMock);
        medicalService.checkBloodPressure("125", new BloodPressure(140, 100));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertServiceMock).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 125, need help", argumentCaptor.getValue());


    }

    @Test
    void checkTemperature() {
        PatientInfoFileRepository fileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(fileRepository.getById(Mockito.anyString()))
                .thenReturn(new PatientInfo("23", "Станислав", "Владимирович",
                        LocalDate.of(2003, 12, 11),
                        new HealthInfo(new BigDecimal(45), new BloodPressure(124, 85))));
        SendAlertService alertServiceMock = Mockito.mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(fileRepository, alertServiceMock);
        medicalService.checkTemperature("23", new BigDecimal("32"));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertServiceMock).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 23, need help", argumentCaptor.getValue());
    }

    @Test
    void indicators_are_normal() {

        PatientInfoFileRepository patientInfoFileRepositoryMock = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepositoryMock.getById((Mockito.anyString())))
                .thenReturn(new PatientInfo("34", "Анна", "Олеговна",
                        LocalDate.of(2006, 2, 14),
                        new HealthInfo(new BigDecimal(38), new BloodPressure(124, 85))));

        SendAlertService alertServiceMock = Mockito.mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepositoryMock, alertServiceMock);

        medicalService.checkTemperature("34", new BigDecimal("36"));
        medicalService.checkBloodPressure("34", new BloodPressure(124, 85));

        Mockito.verify(alertServiceMock, Mockito.times(0))
                .send(Mockito.anyString());


    }
}