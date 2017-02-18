package ru.ege.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Базовый класс движка, представляет из себя расширенный JFrame, для отображения графических данных в реальном времени.
 * Отображение можно реальзовать двумя способами
 * 1)Переопределить метод drawAndUpdate и написать код рисования в нем.
 * 2)Добавить  экземпляр класса, имплементирующего DrawableObject к экземпляру данного класса.
 */
public abstract class EGEJFrame extends JFrame {
    private long lastFrameEnd;
    private long startTime;
    private Map<Integer, List<DrawableObject>> drawableObjectsDepthMap;

    public EGEJFrame() {
        setRotationDirectionMultiplier(-1);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        createBufferStrategy(2);
        lastFrameEnd = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        drawableObjectsDepthMap = new ConcurrentSkipListMap<Integer, List<DrawableObject>>();
    }

    /**
     * Стартует поток рисования и обновления
     */
    public void startDrawingThread() {
        Thread drawingThread = new Thread(() -> {
            while (true) {
                long dt = System.currentTimeMillis() - lastFrameEnd;
                lastFrameEnd = System.currentTimeMillis();
                draw(dt);
            }
        });
        drawingThread.start();
    }

    /**
     * Удаляет объект из списка отображаемых
     *
     * @param drawableObject объект для удаления
     */
    public void removeDrawableObject(DrawableObject drawableObject) {
        for (List<DrawableObject> drawableObjects : drawableObjectsDepthMap.values()) {
            drawableObjects.remove(drawableObject);
        }
    }

    /**
     * Добавляет объект к списку отображаемых
     *
     * @param drawableObject обект для отображения
     */
    public void addDrawableObject(DrawableObject drawableObject) {
        addDrawableObject(drawableObject, 0);
    }

    /**
     * Добавляет объект к списку отображаемы
     *
     * @param drawableObject объект для отображения
     * @param depth          порядок рисования, чем больше тем выше отображается объект
     */
    public void addDrawableObject(DrawableObject drawableObject, int depth) {
        List<DrawableObject> drawableObjects = drawableObjectsDepthMap.get(depth);
        if (drawableObjects == null) {
            drawableObjects = new CopyOnWriteArrayList<>();
            drawableObjectsDepthMap.put(depth, drawableObjects);
        }
        drawableObjects.add(drawableObject);
    }

    /**
     * Меняет приоритет риссования объектов
     *
     * @param drawableObject
     * @param newDepth
     */
    public void changeDepth(DrawableObject drawableObject, int newDepth) {
        removeDrawableObject(drawableObject);
        addDrawableObject(drawableObject, newDepth);
    }

    /**
     * @return список всех DrawableObject в данном окне
     */
    public List<DrawableObject> getDrawableObjects() {
        List<DrawableObject> result = new ArrayList<>();
        drawableObjectsDepthMap.forEach(result::addAll);
        return result;
    }

    /**
     * @param type Тип, например MyDrawableObject.class
     * @param <T>
     * @return Список всех DrawableObject которые можно привести к даннмоу типу
     */
    public <T> List<T> getDrawableObjects(Class<T> type) {
        List<T> result = new ArrayList<>();
        drawableObjectsDepthMap.values().forEach(e -> e.stream().filter(type::isInstance).map(x -> (T) x).forEach(result::add));
        return result;
    }

    private void draw(long dt) {
        BufferStrategy bs = getBufferStrategy();
        Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
        g2d.clearRect(0, 0, getWidth(), getHeight());
        try {
            drawAndUpdate(g2d, (int) dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        drawAndUpdateDrawableObjects(g2d, (int) dt);
        g2d.dispose();
        bs.show();
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawAndUpdateDrawableObjects(Graphics2D g2d, int dt) {
        drawableObjectsDepthMap.values().forEach(e -> e.forEach(d -> drawDrawableObject(d, g2d, dt)));
    }

    public void drawDrawableObject(DrawableObject drawableObject, Graphics2D g2d, int dt) {
        try {
            drawableObject.drawAndUpdate(g2d, dt, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return время прошедшее с создания окошка
     */
    public int getTimeFromStartMillis() {
        return (int) (System.currentTimeMillis() - startTime);
    }

    /**
     * Вызывается каждый фрейм, до рисоования DrawableObject
     *
     * @param graphics экземпляр класса graphics для рисования
     * @param dt       время прошедшее с проглого кадра
     */
    public abstract void drawAndUpdate(Graphics2D graphics, int dt);

    /**
     * Позволяет менять направление поворота векторов по умолчанию
     *
     * @param multiplier - направление поворота по умолчанию
     */
    public static void setRotationDirectionMultiplier(double multiplier) {
        Vector2D.setRotationDirection(multiplier);
    }
}
