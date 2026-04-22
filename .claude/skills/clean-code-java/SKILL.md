---
name: java-clean-code
description: >
  Aplicar clean code y buenas prácticas en Java. Usar SIEMPRE que el usuario
  escriba, refactorice, revise o mejore código Java. Aplica automáticamente
  cuando se mencionan clases, métodos, servicios, repositorios o cualquier
  archivo .java. También activar cuando el usuario pida "mejorar", "revisar",
  "refactorizar" o "crear" código en Java, aunque no lo pida explícitamente.
---

# Java Clean Code & Buenas Prácticas

## Nombres

### Clases
- Sustantivos en **PascalCase** que describan claramente su responsabilidad
- Evitar sufijos genéricos sin valor: `Manager`, `Helper`, `Utils`, `Data` solos
- ✅ `OrderPaymentProcessor`, `UserEmailNotifier`, `InvoiceRepository`
- ❌ `OrderManager`, `Helper`, `DataUtils`

### Métodos
- Verbos en **camelCase** que describan exactamente qué hacen
- Si retorna boolean: usar prefijo `is`, `has`, `can`, `should`
- ✅ `calculateTotalWithDiscount()`, `isEmailVerified()`, `hasAdminPermission()`
- ❌ `process()`, `doStuff()`, `check()`, `handle()`

### Variables
- Nombres descriptivos, sin abreviaciones crípticas
- ✅ `List<Order> pendingOrders`, `BigDecimal discountAmount`
- ❌ `List<Order> list`, `BigDecimal d`, `int x`

### Constantes
- **UPPER_SNAKE_CASE** con nombre que explique su propósito
- ✅ `MAX_RETRY_ATTEMPTS`, `DEFAULT_PAGE_SIZE`
- ❌ `MAX`, `NUM`, `VAL`

---

## Métodos

- **Una sola responsabilidad** — si necesitás "y" para describir lo que hace, dividilo
- **Máximo 20 líneas** — si crece más, extraer métodos privados
- **Máximo 3 parámetros** — si necesitás más, crear un objeto de request/command
- **Sin comentarios que expliquen el qué** — el nombre del método debe ser suficiente
- Los comentarios solo para el **por qué** cuando la lógica de negocio es no obvia

```java
// ❌ Mal
public void process(Order o, User u, boolean b, String s) {
    // validate order
    if (o.getItems().isEmpty()) throw new RuntimeException("empty");
    // apply discount
    if (b) o.setTotal(o.getTotal().multiply(BigDecimal.valueOf(0.9)));
    // notify
    emailService.send(u.getEmail(), s);
}

// ✅ Bien
public void processOrderWithDiscount(Order order, User customer) {
    validateOrderHasItems(order);
    applyDiscountIfEligible(order, customer);
    notifyOrderConfirmation(order, customer);
}

private void validateOrderHasItems(Order order) {
    if (order.getItems().isEmpty()) {
        throw new EmptyOrderException("Cannot process an order without items");
    }
}
```

---

## Clases

- **Single Responsibility Principle** — una clase, un motivo para cambiar
- **Máximo ~200 líneas** — si crece más, evaluar división
- Preferir **composición sobre herencia**
- Campos siempre `private`, exponer solo lo necesario

### Estructura interna (orden estándar)
1. Constantes estáticas
2. Campos de instancia
3. Constructores
4. Métodos públicos
5. Métodos privados

---

## Manejo de errores

- Usar **excepciones específicas**, nunca `Exception` o `RuntimeException` genéricos
- Crear excepciones de dominio propias: `OrderNotFoundException`, `InsufficientStockException`
- **Nunca** swallowear excepciones con catch vacío
- **Nunca** retornar `null` — usar `Optional<T>` cuando algo puede no existir

```java
// ❌ Mal
public Order findOrder(Long id) {
    try {
        return orderRepository.findById(id);
    } catch (Exception e) {
        return null;
    }
}

// ✅ Bien
public Optional<Order> findOrder(Long id) {
    return orderRepository.findById(id);
}

public Order getOrderOrThrow(Long id) {
    return orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
}
```

---

## Inmutabilidad y seguridad

- Usar `final` en campos que no cambian
- Preferir objetos inmutables para Value Objects
- Colecciones retornadas: devolver copia o `Collections.unmodifiableList()`
- Nunca exponer referencias internas mutables

```java
// ✅ Campo inmutable
private final String customerId;

// ✅ Colección segura
public List<OrderItem> getItems() {
    return Collections.unmodifiableList(items);
}
```

---

## Uso de Optional

- Usar para retornos que pueden ser ausentes, **nunca** como parámetro
- No usar `Optional.get()` sin chequear — usar `orElse`, `orElseThrow`, `ifPresent`

```java
// ✅ Bien
userRepository.findByEmail(email)
    .map(User::getFullName)
    .orElse("Invitado");
```

---

## Streams y lambdas

- Preferir streams sobre loops cuando mejoran la legibilidad
- Extraer lambdas complejas a métodos con nombre descriptivo
- No encadenar más de 4-5 operaciones sin extraer variables intermedias

```java
// ✅ Lambda extraída a método
List<Order> overdueOrders = orders.stream()
    .filter(this::isOverdue)
    .collect(Collectors.toList());

private boolean isOverdue(Order order) {
    return order.getDueDate().isBefore(LocalDate.now()) 
        && order.getStatus() != OrderStatus.COMPLETED;
}
```

---

## Principios SOLID a aplicar siempre

| Principio | Qué verificar |
|---|---|
| **S** — Single Responsibility | ¿Esta clase tiene más de un motivo para cambiar? |
| **O** — Open/Closed | ¿Puedo extender sin modificar la clase? |
| **L** — Liskov | ¿Las subclases cumplen el contrato del padre? |
| **I** — Interface Segregation | ¿Las interfaces son pequeñas y específicas? |
| **D** — Dependency Inversion | ¿Dependo de abstracciones, no de implementaciones? |

---

## Checklist antes de entregar código

- [ ] Los nombres de clases y métodos se entienden sin leer el cuerpo
- [ ] Ningún método tiene más de 3 parámetros
- [ ] No hay `null` como valor de retorno (usar `Optional`)
- [ ] No hay excepciones genéricas (`catch (Exception e)`)
- [ ] No hay lógica duplicada
- [ ] Los campos mutables no se exponen directamente
- [ ] Cada clase tiene una sola responsabilidad clara
