package com.evento.backend.web.rest;

import static com.evento.backend.domain.AsientoSeleccionadoAsserts.*;
import static com.evento.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.evento.backend.IntegrationTest;
import com.evento.backend.domain.AsientoSeleccionado;
import com.evento.backend.domain.Sesion;
import com.evento.backend.repository.AsientoSeleccionadoRepository;
import com.evento.backend.service.dto.AsientoSeleccionadoDTO;
import com.evento.backend.service.mapper.AsientoSeleccionadoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AsientoSeleccionadoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AsientoSeleccionadoResourceIT {

    private static final Integer DEFAULT_FILA = 1;
    private static final Integer UPDATED_FILA = 2;

    private static final Integer DEFAULT_COLUMNA = 1;
    private static final Integer UPDATED_COLUMNA = 2;

    private static final String DEFAULT_NOMBRE_PERSONA = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_PERSONA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/asiento-seleccionados";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AsientoSeleccionadoRepository asientoSeleccionadoRepository;

    @Autowired
    private AsientoSeleccionadoMapper asientoSeleccionadoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAsientoSeleccionadoMockMvc;

    private AsientoSeleccionado asientoSeleccionado;

    private AsientoSeleccionado insertedAsientoSeleccionado;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AsientoSeleccionado createEntity(EntityManager em) {
        AsientoSeleccionado asientoSeleccionado = new AsientoSeleccionado()
            .fila(DEFAULT_FILA)
            .columna(DEFAULT_COLUMNA)
            .nombrePersona(DEFAULT_NOMBRE_PERSONA);
        // Add required entity
        Sesion sesion;
        if (TestUtil.findAll(em, Sesion.class).isEmpty()) {
            sesion = SesionResourceIT.createEntity(em);
            em.persist(sesion);
            em.flush();
        } else {
            sesion = TestUtil.findAll(em, Sesion.class).get(0);
        }
        asientoSeleccionado.setSesion(sesion);
        return asientoSeleccionado;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AsientoSeleccionado createUpdatedEntity(EntityManager em) {
        AsientoSeleccionado updatedAsientoSeleccionado = new AsientoSeleccionado()
            .fila(UPDATED_FILA)
            .columna(UPDATED_COLUMNA)
            .nombrePersona(UPDATED_NOMBRE_PERSONA);
        // Add required entity
        Sesion sesion;
        if (TestUtil.findAll(em, Sesion.class).isEmpty()) {
            sesion = SesionResourceIT.createUpdatedEntity(em);
            em.persist(sesion);
            em.flush();
        } else {
            sesion = TestUtil.findAll(em, Sesion.class).get(0);
        }
        updatedAsientoSeleccionado.setSesion(sesion);
        return updatedAsientoSeleccionado;
    }

    @BeforeEach
    void initTest() {
        asientoSeleccionado = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAsientoSeleccionado != null) {
            asientoSeleccionadoRepository.delete(insertedAsientoSeleccionado);
            insertedAsientoSeleccionado = null;
        }
    }

    @Test
    @Transactional
    void createAsientoSeleccionado() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AsientoSeleccionado
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);
        var returnedAsientoSeleccionadoDTO = om.readValue(
            restAsientoSeleccionadoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoSeleccionadoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AsientoSeleccionadoDTO.class
        );

        // Validate the AsientoSeleccionado in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAsientoSeleccionado = asientoSeleccionadoMapper.toEntity(returnedAsientoSeleccionadoDTO);
        assertAsientoSeleccionadoUpdatableFieldsEquals(
            returnedAsientoSeleccionado,
            getPersistedAsientoSeleccionado(returnedAsientoSeleccionado)
        );

        insertedAsientoSeleccionado = returnedAsientoSeleccionado;
    }

    @Test
    @Transactional
    void createAsientoSeleccionadoWithExistingId() throws Exception {
        // Create the AsientoSeleccionado with an existing ID
        asientoSeleccionado.setId(1L);
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAsientoSeleccionadoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoSeleccionadoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AsientoSeleccionado in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFilaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        asientoSeleccionado.setFila(null);

        // Create the AsientoSeleccionado, which fails.
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        restAsientoSeleccionadoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoSeleccionadoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkColumnaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        asientoSeleccionado.setColumna(null);

        // Create the AsientoSeleccionado, which fails.
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        restAsientoSeleccionadoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoSeleccionadoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAsientoSeleccionados() throws Exception {
        // Initialize the database
        insertedAsientoSeleccionado = asientoSeleccionadoRepository.saveAndFlush(asientoSeleccionado);

        // Get all the asientoSeleccionadoList
        restAsientoSeleccionadoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(asientoSeleccionado.getId().intValue())))
            .andExpect(jsonPath("$.[*].fila").value(hasItem(DEFAULT_FILA)))
            .andExpect(jsonPath("$.[*].columna").value(hasItem(DEFAULT_COLUMNA)))
            .andExpect(jsonPath("$.[*].nombrePersona").value(hasItem(DEFAULT_NOMBRE_PERSONA)));
    }

    @Test
    @Transactional
    void getAsientoSeleccionado() throws Exception {
        // Initialize the database
        insertedAsientoSeleccionado = asientoSeleccionadoRepository.saveAndFlush(asientoSeleccionado);

        // Get the asientoSeleccionado
        restAsientoSeleccionadoMockMvc
            .perform(get(ENTITY_API_URL_ID, asientoSeleccionado.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(asientoSeleccionado.getId().intValue()))
            .andExpect(jsonPath("$.fila").value(DEFAULT_FILA))
            .andExpect(jsonPath("$.columna").value(DEFAULT_COLUMNA))
            .andExpect(jsonPath("$.nombrePersona").value(DEFAULT_NOMBRE_PERSONA));
    }

    @Test
    @Transactional
    void getNonExistingAsientoSeleccionado() throws Exception {
        // Get the asientoSeleccionado
        restAsientoSeleccionadoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAsientoSeleccionado() throws Exception {
        // Initialize the database
        insertedAsientoSeleccionado = asientoSeleccionadoRepository.saveAndFlush(asientoSeleccionado);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asientoSeleccionado
        AsientoSeleccionado updatedAsientoSeleccionado = asientoSeleccionadoRepository.findById(asientoSeleccionado.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAsientoSeleccionado are not directly saved in db
        em.detach(updatedAsientoSeleccionado);
        updatedAsientoSeleccionado.fila(UPDATED_FILA).columna(UPDATED_COLUMNA).nombrePersona(UPDATED_NOMBRE_PERSONA);
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(updatedAsientoSeleccionado);

        restAsientoSeleccionadoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, asientoSeleccionadoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(asientoSeleccionadoDTO))
            )
            .andExpect(status().isOk());

        // Validate the AsientoSeleccionado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAsientoSeleccionadoToMatchAllProperties(updatedAsientoSeleccionado);
    }

    @Test
    @Transactional
    void putNonExistingAsientoSeleccionado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoSeleccionado.setId(longCount.incrementAndGet());

        // Create the AsientoSeleccionado
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAsientoSeleccionadoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, asientoSeleccionadoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(asientoSeleccionadoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AsientoSeleccionado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAsientoSeleccionado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoSeleccionado.setId(longCount.incrementAndGet());

        // Create the AsientoSeleccionado
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAsientoSeleccionadoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(asientoSeleccionadoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AsientoSeleccionado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAsientoSeleccionado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoSeleccionado.setId(longCount.incrementAndGet());

        // Create the AsientoSeleccionado
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAsientoSeleccionadoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(asientoSeleccionadoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AsientoSeleccionado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAsientoSeleccionadoWithPatch() throws Exception {
        // Initialize the database
        insertedAsientoSeleccionado = asientoSeleccionadoRepository.saveAndFlush(asientoSeleccionado);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asientoSeleccionado using partial update
        AsientoSeleccionado partialUpdatedAsientoSeleccionado = new AsientoSeleccionado();
        partialUpdatedAsientoSeleccionado.setId(asientoSeleccionado.getId());

        partialUpdatedAsientoSeleccionado.nombrePersona(UPDATED_NOMBRE_PERSONA);

        restAsientoSeleccionadoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAsientoSeleccionado.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAsientoSeleccionado))
            )
            .andExpect(status().isOk());

        // Validate the AsientoSeleccionado in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAsientoSeleccionadoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAsientoSeleccionado, asientoSeleccionado),
            getPersistedAsientoSeleccionado(asientoSeleccionado)
        );
    }

    @Test
    @Transactional
    void fullUpdateAsientoSeleccionadoWithPatch() throws Exception {
        // Initialize the database
        insertedAsientoSeleccionado = asientoSeleccionadoRepository.saveAndFlush(asientoSeleccionado);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the asientoSeleccionado using partial update
        AsientoSeleccionado partialUpdatedAsientoSeleccionado = new AsientoSeleccionado();
        partialUpdatedAsientoSeleccionado.setId(asientoSeleccionado.getId());

        partialUpdatedAsientoSeleccionado.fila(UPDATED_FILA).columna(UPDATED_COLUMNA).nombrePersona(UPDATED_NOMBRE_PERSONA);

        restAsientoSeleccionadoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAsientoSeleccionado.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAsientoSeleccionado))
            )
            .andExpect(status().isOk());

        // Validate the AsientoSeleccionado in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAsientoSeleccionadoUpdatableFieldsEquals(
            partialUpdatedAsientoSeleccionado,
            getPersistedAsientoSeleccionado(partialUpdatedAsientoSeleccionado)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAsientoSeleccionado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoSeleccionado.setId(longCount.incrementAndGet());

        // Create the AsientoSeleccionado
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAsientoSeleccionadoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, asientoSeleccionadoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(asientoSeleccionadoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AsientoSeleccionado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAsientoSeleccionado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoSeleccionado.setId(longCount.incrementAndGet());

        // Create the AsientoSeleccionado
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAsientoSeleccionadoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(asientoSeleccionadoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AsientoSeleccionado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAsientoSeleccionado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        asientoSeleccionado.setId(longCount.incrementAndGet());

        // Create the AsientoSeleccionado
        AsientoSeleccionadoDTO asientoSeleccionadoDTO = asientoSeleccionadoMapper.toDto(asientoSeleccionado);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAsientoSeleccionadoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(asientoSeleccionadoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AsientoSeleccionado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAsientoSeleccionado() throws Exception {
        // Initialize the database
        insertedAsientoSeleccionado = asientoSeleccionadoRepository.saveAndFlush(asientoSeleccionado);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the asientoSeleccionado
        restAsientoSeleccionadoMockMvc
            .perform(delete(ENTITY_API_URL_ID, asientoSeleccionado.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return asientoSeleccionadoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected AsientoSeleccionado getPersistedAsientoSeleccionado(AsientoSeleccionado asientoSeleccionado) {
        return asientoSeleccionadoRepository.findById(asientoSeleccionado.getId()).orElseThrow();
    }

    protected void assertPersistedAsientoSeleccionadoToMatchAllProperties(AsientoSeleccionado expectedAsientoSeleccionado) {
        assertAsientoSeleccionadoAllPropertiesEquals(
            expectedAsientoSeleccionado,
            getPersistedAsientoSeleccionado(expectedAsientoSeleccionado)
        );
    }

    protected void assertPersistedAsientoSeleccionadoToMatchUpdatableProperties(AsientoSeleccionado expectedAsientoSeleccionado) {
        assertAsientoSeleccionadoAllUpdatablePropertiesEquals(
            expectedAsientoSeleccionado,
            getPersistedAsientoSeleccionado(expectedAsientoSeleccionado)
        );
    }
}
