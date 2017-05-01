package com.devesion.orientdb

import akka.actor.{Actor, ActorLogging, ActorRef, Status}

import scala.util.{Failure, Success, Try}

private[orientdb] abstract class AbstractRepositoryActor[T <: Entity[String]](repository: Repository[T]) extends Actor with ActorLogging {
  private implicit val system = context.system
  private implicit val dispatcher = system.dispatcher

  override def receive: Receive = {
    case GetItem(id) =>
      sendResult(sender) {
        repository.findById(id)
      }

    case GetDocument(id) =>
      sendResult(sender) {
        repository.findDocumentById(id)
      }

    case ListItems() =>
      sendResult(sender) {
        repository.findAll()
      }

    case QueryItems(where, params) =>
      sendResult(sender) {
        repository.query(where, params)
      }

    case SaveItem(item) =>
      sendResult(sender) {
        repository.save(item.asInstanceOf[T])
      }

    case MergeItem(item) =>
      sendResult(sender) {
        repository.merge(item.asInstanceOf[T])
      }

    case DeleteItem(id) =>
      sendResult(sender) {
        val item = repository.findById(id)
        repository.delete(item)
      }
  }

  def sendResult(sender: ActorRef)(f: ⇒ Any) {
    val ret = Try {
      f
    } match {
      case Success(v) ⇒ v
      case Failure(ex) ⇒ Status.Failure(ex)
    }

    sender ! ret
  }
}

