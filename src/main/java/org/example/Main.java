package org.example;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE) // Метод устанавливает стратегию обновления схемы базы данных для движка Camunda BPM при его создании
                .setJdbcUrl("jdbc:h2:mem:my-own-db;DB_CLOSE_DELAY=1")
                .setJobExecutorActivate(true) // Указывает, активировать ли процесс выполнения заданий (Job Executor) во время создания движка (Process Engine)
                .buildProcessEngine(); // Создание экземпляра ProcessEngine, который представляет собой основной интерфейс для взаимодействия с функциями BPM движка

        RepositoryService repositoryService = processEngine.getRepositoryService(); // Это интерфейс в Camunda BPM API, предоставляющий доступ к операциям, связанным с хранилищем BPMN-процессов. Это может включать деплой процессов, получение информации о процессах и их версиях, удаление процессов и т. д.

        BpmnModelInstance process = Bpmn.createExecutableProcess("myProcess")
                .startEvent()
                .name("Start Event")
                .id("startEvent")
                .userTask()
                .name("User Task")
                .id("userTask")
                .endEvent()
                .name("End Event")
                .id("endEvent")
                .done(); // Завершение создания процесса

        // Создание SequenceFlow и связывание с элементами. SequenceFlow - это объект, который представляет связь между элементами в процессе
        SequenceFlow flow1 = process.newInstance(SequenceFlow.class);
        flow1.setId("startEventToUserTask");

        SequenceFlow flow2 = process.newInstance(SequenceFlow.class);
        flow2.setId("userTaskToEndEvent");

        // Получение элементов модели для связывания SequenceFlow
        StartEvent startEvent = process.getModelElementById("startEvent");
        UserTask userTask = process.getModelElementById("userTask");
        EndEvent endEvent = process.getModelElementById("endEvent");

        // Связывание SequenceFlow с элементами модели
        flow1.setSource(startEvent);
        flow1.setTarget(userTask);

        flow2.setSource(userTask);
        flow2.setTarget(endEvent);

        //Для проверки "локально" в Camunda Web Modeler
        Bpmn.writeModelToFile(new File("myProcess.bpmn"), process);
    }
}