package com.devesion.orientdb

import akka.actor.{Actor, ActorLogging}

private[orientdb] abstract class AbstractRepositoryActor[T <: Entity[String]](repository: Repository[T]) extends Actor with ActorLogging {
  private implicit val system = context.system
  private implicit val dispatcher = system.dispatcher

  override def receive: Receive = {
    case GetItem(id) =>
      sender ! repository.findById(id)

    case ListItems() =>
      sender ! repository.findAll()

    case SaveItem(item) =>
      sender ! repository.save(item.asInstanceOf[T])

    case DeleteItem(id) =>
      val item = repository.findById(id)
      sender ! repository.delete(item)
  }
}

