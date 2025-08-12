package com.digital.evidence.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.digital.evidence.model.Evidence;
import com.digital.evidence.repository.EvidenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EvidenceServiceTest {

    @Mock
    private EvidenceRepository evidenceRepository;

    @InjectMocks
    private EvidenceServiceImpl evidenceService;

    private Evidence e1;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        e1 = new Evidence();
        date = LocalDate.of(2024, 1, 1);
    }

    @Test
    void saveEvidence_delegatesToRepository() {
        when(evidenceRepository.save(e1)).thenReturn(e1);
        Evidence saved = evidenceService.saveEvidence(e1);
        assertThat(saved).isSameAs(e1);
        verify(evidenceRepository).save(e1);
    }

    @Test
    void findAllEvidence_delegatesToRepository() {
        when(evidenceRepository.findAll()).thenReturn(List.of(e1));
        List<Evidence> all = evidenceService.findAllEvidence();
        assertThat(all).containsExactly(e1);
        verify(evidenceRepository).findAll();
    }

    @Test
    void findEvidenceInCustody_delegatesToRepository() {
        when(evidenceRepository.findByCustodyStatus("In Custody")).thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.findEvidenceInCustody();
        assertThat(list).containsExactly(e1);
        verify(evidenceRepository).findByCustodyStatus("In Custody");
    }

    @Test
    void findEvidenceById_found() {
        when(evidenceRepository.findById(10L)).thenReturn(Optional.of(e1));
        Evidence found = evidenceService.findEvidenceById(10L);
        assertThat(found).isSameAs(e1);
        verify(evidenceRepository).findById(10L);
    }

    @Test
    void findEvidenceById_notFound_returnsNull() {
        when(evidenceRepository.findById(11L)).thenReturn(Optional.empty());
        Evidence found = evidenceService.findEvidenceById(11L);
        assertThat(found).isNull();
        verify(evidenceRepository).findById(11L);
    }

    @Test
    void deleteEvidenceById_delegatesToRepository() {
        evidenceService.deleteEvidenceById(5L);
        verify(evidenceRepository).deleteById(5L);
    }

    // ----- filterEvidence branches -----

    @Test
    void filter_onlyOfficer_calls_findBySourceOfficer() {
        when(evidenceRepository.findBySourceOfficerContainingIgnoreCase("john"))
                .thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.filterEvidence("john", null, null);
        assertThat(list).containsExactly(e1);
        verify(evidenceRepository).findBySourceOfficerContainingIgnoreCase("john");
    }

    @Test
    void filter_onlyEncrypted_calls_findByEncryptionStatus() {
        when(evidenceRepository.findByEncryptionStatus(any(Boolean.class)))
                .thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.filterEvidence(null, Boolean.TRUE, null);
        assertThat(list).containsExactly(e1);

        ArgumentCaptor<Boolean> encCap = ArgumentCaptor.forClass(Boolean.class);
        verify(evidenceRepository).findByEncryptionStatus(encCap.capture());
        assertThat(encCap.getValue()).isTrue();
    }

    @Test
    void filter_onlyDate_calls_findByAcquisitionDate() {
        when(evidenceRepository.findByAcquisitionDate(date)).thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.filterEvidence(null, null, date);
        assertThat(list).containsExactly(e1);
        verify(evidenceRepository).findByAcquisitionDate(date);
    }

    @Test
    void filter_officerAndEncrypted_calls_combined() {
        when(evidenceRepository.findBySourceOfficerContainingIgnoreCaseAndEncryptionStatus("john", true))
                .thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.filterEvidence("john", Boolean.TRUE, null);
        assertThat(list).containsExactly(e1);
        verify(evidenceRepository).findBySourceOfficerContainingIgnoreCaseAndEncryptionStatus("john", true);
    }

    @Test
    void filter_officerAndDate_calls_combined() {
        when(evidenceRepository.findBySourceOfficerContainingIgnoreCaseAndAcquisitionDate("john", date))
                .thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.filterEvidence("john", null, date);
        assertThat(list).containsExactly(e1);
        verify(evidenceRepository).findBySourceOfficerContainingIgnoreCaseAndAcquisitionDate("john", date);
    }

    @Test
    void filter_encryptedAndDate_calls_combined() {
        when(evidenceRepository.findByEncryptionStatusAndAcquisitionDate(any(Boolean.class), any(LocalDate.class)))
                .thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.filterEvidence(null, Boolean.TRUE, date);
        assertThat(list).containsExactly(e1);

        ArgumentCaptor<Boolean> encCap = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<LocalDate> dateCap = ArgumentCaptor.forClass(LocalDate.class);
        verify(evidenceRepository).findByEncryptionStatusAndAcquisitionDate(encCap.capture(), dateCap.capture());
        assertThat(encCap.getValue()).isTrue();
        assertThat(dateCap.getValue()).isEqualTo(date);
    }

    @Test
    void filter_allThree_calls_tripleCombined() {
        when(evidenceRepository.findBySourceOfficerContainingIgnoreCaseAndEncryptionStatusAndAcquisitionDate("john", true, date))
                .thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.filterEvidence("john", Boolean.TRUE, date);
        assertThat(list).containsExactly(e1);
        verify(evidenceRepository).findBySourceOfficerContainingIgnoreCaseAndEncryptionStatusAndAcquisitionDate("john", true, date);
    }

    @Test
    void filter_none_calls_findAll() {
        when(evidenceRepository.findAll()).thenReturn(List.of(e1));
        List<Evidence> list = evidenceService.filterEvidence(null, null, null);
        assertThat(list).containsExactly(e1);
        verify(evidenceRepository).findAll();
    }

    @Test
    void filter_blankOfficer_treatedAsEmpty_calls_findAll() {
        when(evidenceRepository.findAll()).thenReturn(List.of());
        evidenceService.filterEvidence("   ", null, null);
        verify(evidenceRepository).findAll();
    }
}
